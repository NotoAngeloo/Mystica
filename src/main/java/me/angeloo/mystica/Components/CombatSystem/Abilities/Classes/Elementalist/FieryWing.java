package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.ClassSpecific.Inflame;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusInstance;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FieryWing extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownManager cooldownManager;

    private final Heat heat;

    //private final Map<UUID, Integer> inflameMap = new HashMap<>();


    public FieryWing(Mystica main, AbilityManager manager){
        super("fiery_wing");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownManager = main.getCooldownManager();
        this.heat = manager.getHeat();
    }

    private final double range = 20;
    private final int baseDamage = 60;
    private final int baseCooldown = 30;

    @Override
    public boolean use(LivingEntity caster){


        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }


        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        heat.addHeat(caster, 10);

        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand spawnTexture = caster.getWorld().spawn(start, ArmorStand.class);
        spawnTexture.setInvisible(true);
        spawnTexture.setGravity(false);
        spawnTexture.setCollidable(false);
        spawnTexture.setInvulnerable(true);
        spawnTexture.setMarker(true);

        EntityEquipment entityEquipment2 = spawnTexture.getEquipment();

        ItemStack spawnItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta2 = spawnItem.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(7);
        spawnItem.setItemMeta(meta2);
        assert entityEquipment2 != null;
        entityEquipment2.setHelmet(spawnItem);

        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            boolean spawned = false;
            int ran = 0;
            public void run(){

                spawnTexture.teleport(caster.getLocation().clone().subtract(0,1,0));

                if(ran >= 10 && !spawned){

                    //abilityManager.setSkillRunning(player, false);
                    spawned = true;

                    ArmorStand armorStand = caster.getWorld().spawn(caster.getLocation().clone().subtract(0,1,0), ArmorStand.class);
                    armorStand.setInvisible(true);
                    armorStand.setGravity(false);
                    armorStand.setCollidable(false);
                    armorStand.setInvulnerable(true);
                    armorStand.setMarker(true);

                    EntityEquipment entityEquipment = armorStand.getEquipment();

                    ItemStack horseItem = new ItemStack(Material.DRAGON_BREATH);
                    ItemMeta meta = horseItem.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(4);
                    horseItem.setItemMeta(meta);
                    assert entityEquipment != null;
                    entityEquipment.setHelmet(horseItem);

                    Location targetLoc = target.getLocation().clone().subtract(0,1,0);

                    new BukkitRunnable(){
                        int count = 0;
                        Location targetWasLoc = targetLoc.clone();
                        @Override
                        public void run(){

                            if(targetStillValid(target)){
                                Location targetLoc = target.getLocation();
                                targetLoc = targetLoc.subtract(0,1,0);
                                targetWasLoc = targetLoc.clone();
                            }

                            Location current = armorStand.getLocation();

                            if (!sameWorld(current, targetWasLoc)) {
                                cancelTask();
                                return;
                            }

                            Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                            double distance = current.distance(targetWasLoc);
                            double distanceThisTick = Math.min(distance, .75);

                            if(distanceThisTick!=0){
                                current.add(direction.normalize().multiply(distanceThisTick));
                            }

                            current.setDirection(direction);

                            armorStand.teleport(current);


                            caster.getWorld().spawnParticle(Particle.FLAME, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                            if (distance <= 1) {

                                addInflame(caster);

                                cancelTask();

                                boolean crit = damageCalculator.checkIfCrit(caster, 0);
                                double damage = damageCalculator.calculateDamage(caster, target, "Magical", finalSkillDamage, crit);

                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);


                            }

                            if(count>100){
                                cancelTask();
                            }

                            count++;
                        }

                        private boolean targetStillValid(LivingEntity target){

                            if(target instanceof Player){

                                if(!((Player) target).isOnline()){
                                    return false;
                                }

                            }

                            return !target.isDead();
                        }

                        private boolean sameWorld(Location loc1, Location loc2) {
                            return loc1.getWorld().equals(loc2.getWorld());
                        }

                        private void cancelTask() {
                            this.cancel();
                            armorStand.remove();
                        }
                    }.runTaskTimer(main, 0, 1);

                }

                if(ran >= 30){
                    cancelTask();
                }

                ran++;
            }

            private void cancelTask() {
                this.cancel();
                spawnTexture.remove();
            }

        }.runTaskTimer(main, 0, 1);



    }

    @Override
    public void onExternalTrigger(LivingEntity caster){
        addInflame(caster);
    }

    private void addInflame(LivingEntity caster){

        boolean pyromancer = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Pyromancer);

        if(!pyromancer){
            return;
        }

        statusEffectManager.applyEffect(caster, new Inflame(), null, null, caster);


        //this is now all part of the effect itself
        /*int stacks = statusEffectManager.getStackAmount(caster, "inflame");


        if(stacks >=4){
            cooldownManager.clear(caster.getUniqueId(), -1);
            statusEffectManager.removeEffect(caster, "inflame");

        }*/

    }



    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return baseDamage + ((int)(skillLevel/3));
    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target){
        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) target)){
                    return false;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return false;
                }
            }

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }

        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }

}
