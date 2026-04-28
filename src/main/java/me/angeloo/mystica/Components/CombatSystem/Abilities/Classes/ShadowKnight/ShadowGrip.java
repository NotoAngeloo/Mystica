package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.AggroManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Pulled;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ShadowGrip extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownManager cooldownManager;

    private final Energy energy;

    public ShadowGrip(Mystica main, AbilityManager manager){
        super("shadow_grip");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownManager = main.getCooldownManager();
        energy = manager.getEnergy();
    }

    private final int baseCooldown = 10;
    private final double range = 15;
    private final int cost = 30;
    private final int baseDamage = 15;

    @Override
    public boolean use(LivingEntity caster){

        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        energy.subTractEnergyFromEntity(caster, cost);

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), 6, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster){

        boolean blood = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Blood);


        
        LivingEntity target = targetManager.getPlayerTarget(caster);

        Location start = caster.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack hand = new ItemStack(Material.REDSTONE);
        ItemMeta meta = hand.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(5);

        hand.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(hand);

        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            int count = 0;
            boolean pulled = false;
            boolean going = true;
            @Override
            public void run(){

                if(!targetStillValid(caster)){
                    cancelTask();
                    return;
                }

                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                Location playerLoc = caster.getLocation();
                playerLoc = playerLoc.subtract(0,1,0);
                Location targetLoc = target.getLocation();
                targetLoc = targetLoc.subtract(0,1,0);

                Location current = armorStand.getLocation();

                if (!sameWorld(current, targetLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction;
                double distance;

                if(going){
                    direction = targetLoc.toVector().subtract(current.toVector());
                    distance = current.distance(targetLoc);
                }
                else{
                    direction = playerLoc.toVector().subtract(current.toVector());
                    distance = current.distance(playerLoc);
                }

                double distanceThisTick = Math.min(distance, .6);

                if(distanceThisTick!=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                armorStand.teleport(current);

                if(!going){

                    if(distance <= 1){
                        cancelTask();
                        return;
                    }

                    if(wallCheck(current, direction, distanceThisTick)){
                        cancelTask();
                        return;
                    }

                    Vector opposite = direction.clone().multiply(-1);
                    current.setDirection(opposite);
                    armorStand.teleport(current);

                    if(targetStillValid(target) && profileManager.getAnyProfile(target).getIsMovable()){

                        if(target instanceof Player){
                            if(profileManager.getAnyProfile(target).getIfDead()){
                                return;
                            }

                        }

                        target.teleport(current.add(0,1,0));

                    }
                }

                if(going && distance <= 1){
                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);


                    if(blood){
                        aggroManager.setAsHighPriorityTarget(target, caster);
                        if(target instanceof Player){
                            targetManager.setPlayerTarget((Player) target, caster);
                        }
                    }

                    //also check and pull creature
                    pullTarget();

                    if(targetStillValid(target) && profileManager.getAnyProfile(target).getIsMovable()){
                        pulled = true;
                        statusEffectManager.applyEffect(target, new Pulled(), null, null, caster);
                    }

                    going = false;
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
                //abilityManager.setSkillRunning(player, false);
                if(pulled){
                    statusEffectManager.removeEffect(target, "pull");
                }
            }

            private void pullTarget(){
                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack hand = new ItemStack(Material.REDSTONE);
                ItemMeta meta = hand.getItemMeta();
                assert meta != null;

                meta.setCustomModelData(8);

                hand.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(hand);

                going = false;
            }

            private boolean wallCheck(Location current, Vector direction, double distance){

                Location newLoc = current.clone().add(direction.normalize().multiply(distance));

                newLoc.add(0,1,0);

                return !newLoc.getBlock().isPassable();
            }

        }.runTaskTimer(main, 0, 1);
        
    }


    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();

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

        if(energy.getCurrentEnergy(caster)<cost){
            return false;
        }


        return cooldownManager.isReady(caster.getUniqueId(), 6, statusEffectManager.getHastePercent(caster));
    }

    /*public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 8;
        }

        return 0;
    }*/

}
