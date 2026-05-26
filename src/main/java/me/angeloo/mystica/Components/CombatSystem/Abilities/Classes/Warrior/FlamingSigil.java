package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.FlamingSigilAttack;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.FlamingSigilHealth;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class FlamingSigil extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownManager cooldownManager;

    public FlamingSigil(Mystica main){
        super("flaming_sigil");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownManager = main.getCooldownManager();
    }

    private final int baseCooldown = 10;
    private final int buffAmount = 5;

    @Override
    public boolean use(LivingEntity caster){

        if(!usable(caster)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 6, (long) (baseCooldown * 1000));
        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        boolean executioner = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.EXECUTIONER);
        boolean gladiator = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.GLADIATOR);



        Location spawnStart = caster.getLocation().clone();

        ArmorStand sigil = caster.getWorld().spawn(spawnStart.clone().subtract(0,5,0), ArmorStand.class);
        sigil.setInvisible(true);
        sigil.setGravity(false);
        sigil.setCollidable(false);
        sigil.setInvulnerable(true);
        sigil.setMarker(true);

        EntityEquipment entityEquipment = sigil.getEquipment();

        ItemStack item = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(7);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);
        sigil.teleport(spawnStart);

        Location center = sigil.getLocation();

        Set<LivingEntity> hitBySkill = new HashSet<>();
        double finalBuffAmount = getBuffAmount(caster);
        new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (4 * Math.cos(angle));
                    double y = center.getY() + 1;
                    double z = center.getZ() + (4 * Math.sin(angle));
                    Location loc = new Location(center.getWorld(), x, y, z);
                    caster.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
                }

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - 4,
                        center.getY() - 2,
                        center.getZ() - 4,
                        center.getX() + 4,
                        center.getY() + 4,
                        center.getZ() + 4
                );

                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof LivingEntity thisEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    if(hitBySkill.contains(thisEntity)){
                        continue;
                    }

                    if(entity == caster){
                        statusEffectManager.applyEffect(caster, new FlamingSigilAttack(), null, finalBuffAmount, caster);
                        statusEffectManager.applyEffect(caster, new FlamingSigilHealth(), null, finalBuffAmount, caster);
                        hitBySkill.add(caster);
                        continue;
                    }


                    if(entity instanceof Player){
                        if (pvpManager.pvpLogic(caster, (Player) entity)) {
                            continue;
                        }
                    }

                    if(!(entity instanceof Player)){
                        if(pveChecker.pveLogic(thisEntity)){
                            continue;
                        }
                    }


                    if(executioner){
                        statusEffectManager.applyEffect(thisEntity, new FlamingSigilAttack(), null, finalBuffAmount, caster);
                    }

                    if(gladiator){
                        statusEffectManager.applyEffect(thisEntity, new FlamingSigilHealth(), null, finalBuffAmount, caster);
                    }

                    hitBySkill.add(thisEntity);

                }

                if(ran>=20*5){
                    this.cancel();
                    sigil.remove();
                }

                ran++;
            }
        }.runTaskTimer(main, 0, 1);
    }

    public double getBuffAmount(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();
        return buffAmount + ((int)(skillLevel/3));
    }


    @Override
    public boolean usable(LivingEntity caster){
        if (!cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster))) {
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

    @Override
    public String skillBarIcon(LivingEntity entity) {
        return "\ue413";
    }
}
