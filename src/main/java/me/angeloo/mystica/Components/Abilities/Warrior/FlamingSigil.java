package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.Enums.SubClass;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class FlamingSigil {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public FlamingSigil(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){
        if (!abilityReadyInMap.containsKey(caster.getUniqueId())) {
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(caster) <= 0) {
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimerAsynchronously(main, 0, 20);
        cooldownTask.put(caster.getUniqueId(), task);
    }

    private void execute(LivingEntity caster){

        boolean executioner = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Executioner);
        boolean gladiator = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Gladiator);



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


                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity thisEntity = (LivingEntity) entity;

                    if(hitBySkill.contains(thisEntity)){
                        continue;
                    }

                    if(entity == caster){
                        buffAndDebuffManager.getFlamingSigilBuff().applyAttackBuff(caster, finalBuffAmount);
                        buffAndDebuffManager.getFlamingSigilBuff().applyHealthBuff(caster, finalBuffAmount);
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
                        buffAndDebuffManager.getFlamingSigilBuff().applyAttackBuff(thisEntity, finalBuffAmount);
                    }

                    if(gladiator){
                        buffAndDebuffManager.getFlamingSigilBuff().applyHealthBuff(thisEntity, finalBuffAmount);
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
        return 5 + ((int)(skillLevel/3));
    }

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }


    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if (getCooldown(caster) > 0) {
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }

}
