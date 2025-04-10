package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Soulcrack {

    private final Mystica main;
    private final ItemManager itemManager;
    private final AbilityManager abilityManager;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final CooldownDisplayer cooldownDisplayer;

    private final Energy energy;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Soulcrack(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        abilityManager = manager;
        itemManager = main.getClassEquipmentManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        energy = shadowKnightAbilities.getEnergy();
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
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

        abilityReadyInMap.put(caster.getUniqueId(), 25);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 8);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 8);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        int castTime = 45;

        Location start = caster.getLocation();
        ArmorStand armorStand = caster.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(90), Math.toRadians(40)));
        armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(-90), Math.toRadians(-40)));

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack weapon = itemManager.getShadowKnightEquipment().getBaseWeapon();
        ItemStack offhand = weapon.clone();

        if(caster instanceof Player){
            weapon = profileManager.getAnyProfile(caster).getPlayerEquipment().getWeapon();
            ItemMeta offhandItemMeta = offhand.getItemMeta();
            offhandItemMeta.setCustomModelData(weapon.getItemMeta().getCustomModelData() + 1);
            offhand.setItemMeta(offhandItemMeta);
        }

        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(weapon);
        entityEquipment.setItemInOffHand(offhand);

        if(caster instanceof Player){
            ((Player)caster).getInventory().setItemInMainHand(null);
            ((Player)caster).getInventory().setItemInOffHand(null);
        }


        armorStand.teleport(start);

        double skillDamage =getSkillDamage(caster);

        skillDamage = skillDamage * .25;

        double manaRestoration = getEnergyRestored();

        manaRestoration = manaRestoration/ (double) castTime;

        abilityManager.setCasting(caster, true);
        double finalSkillDamage = skillDamage;
        double finalManaRestoration = manaRestoration;
        new BukkitRunnable(){
            int ran = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = caster.getLocation().getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location playerLoc = caster.getLocation().clone().add(0,0.5,0);
                playerLoc.setDirection(direction);

                armorStand.teleport(playerLoc);

                if(ran%10==0){
                    damageNear();
                }

                energy.addEnergyToEntity(caster, (int) finalManaRestoration);

                double percent = ((double) ran / castTime) * 100;

                abilityManager.setCastBar(caster, percent);

                if(ran >= castTime){
                    cancelTask();
                }

                ran++;
                angle += 45;
                if (angle >= 360) {
                    angle = 0;
                }
            }

            private void damageNear(){

                BoundingBox hitBox = new BoundingBox(
                        caster.getLocation().getX() - 5,
                        caster.getLocation().getY() - 2,
                        caster.getLocation().getZ() - 5,
                        caster.getLocation().getX() + 5,
                        caster.getLocation().getY() + 4,
                        caster.getLocation().getZ() + 5
                );

                for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

                    if(entity == caster){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, livingEntity, "Physical", finalSkillDamage, crit);

                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(caster, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);

                            if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                                Vector awayDirection = entity.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
                                Vector velocity = awayDirection.multiply(.75).add(new Vector(0, .5, 0));
                                livingEntity.setVelocity(velocity);
                                buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                            }

                        }
                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, caster));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, caster);

                        if(profileManager.getAnyProfile(livingEntity).getIsMovable()){
                            Vector awayDirection = entity.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize();
                            Vector velocity = awayDirection.multiply(.75).add(new Vector(0, .5, 0));
                            livingEntity.setVelocity(velocity);
                            buffAndDebuffManager.getKnockUp().applyKnockUp(livingEntity);
                        }

                    }

                }
            }

            private void cancelTask() {
                this.cancel();
                abilityManager.setCasting(caster, false);
                abilityManager.setCastBar(caster, 0);
                armorStand.remove();
                if(caster instanceof Player){
                    buffAndDebuffManager.getHidden().showWeapons((Player) caster);
                }

            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_8_Level_Bonus();
        return 50 + ((int)(skillLevel/3));
    }

    public int getEnergyRestored(){
        return 50;
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
        return getCooldown(caster) <= 0;
    }

}
