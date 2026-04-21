package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.WarriorAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.Hud.CooldownDisplayer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TempestRage extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final ChangeResourceHandler changeResourceHandler;
    private final Rage rage;


    public TempestRage(Mystica main, AbilityManager manager){
        super("tempest_rage");
        this.main = main;
        profileManager = main.getProfileManager();
        statusEffectManager = main.getStatusEffectManager();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = manager.getCooldownManager();
        rage = manager.getRage();
    }

    private final int baseCooldown = 10;
    private final int baseDamage = 60;

    @Override
    public void use(LivingEntity caster) {

        if(!usable(caster)){
            return;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 3, (long) (baseCooldown * 1000));
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        Location start = caster.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(5);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        double skillDamage = getSkillDamage(caster);

        int ticks = 6;

        double finalSkillDamage = skillDamage / ticks;
        new BukkitRunnable(){
            int timeRan = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                    }
                }

                if(profileManager.getAnyProfile(caster).getIfDead()){
                    cancelTask();
                    return;
                }

                if (initialDirection == null) {
                    initialDirection = caster.getLocation().getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location playerLoc = caster.getLocation().clone();
                playerLoc.setDirection(direction);

                armorStand.teleport(playerLoc);

                if(timeRan%20==0){
                    BoundingBox hitBox = new BoundingBox(
                            caster.getLocation().getX() - 6,
                            caster.getLocation().getY() - 2,
                            caster.getLocation().getZ() - 6,
                            caster.getLocation().getX() + 6,
                            caster.getLocation().getY() + 6,
                            caster.getLocation().getZ() + 6
                    );

                    for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == caster){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity livingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        boolean crit = damageCalculator.checkIfCrit(caster, 0);
                        double damage = (damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(caster, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                                rage.addRageToEntity(caster, 10);
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster, crit);
                            rage.addRageToEntity(caster, 10);
                        }

                    }
                }

                if(timeRan >= 20*ticks){
                    cancelTask();
                }

                timeRan++;

                angle += 20;
                if (angle >= 360) {
                    angle = 0;
                }
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }



    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_3_Level_Bonus();
        return baseDamage + ((int)(skillLevel/3));
    }

    @Override
    public boolean usable(LivingEntity caster){
        return cooldownManager.isReady(caster.getUniqueId(), 3, statusEffectManager.getHastePercent(caster));
    }

}
