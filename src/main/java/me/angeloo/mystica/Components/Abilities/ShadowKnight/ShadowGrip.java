package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import me.angeloo.mystica.Utility.SubClass;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShadowGrip {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Energy energy;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ShadowGrip(Mystica main, AbilityManager manager, ShadowKnightAbilities shadowKnightAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        energy = shadowKnightAbilities.getEnergy();
    }

    private final double range = 15;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }


        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        energy.subTractEnergyFromEntity(caster, getCost());

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 6);

            }
        }.runTaskTimerAsynchronously(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

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
                        buffAndDebuffManager.getPulled().applyPull(target);
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
                    buffAndDebuffManager.getPulled().removePull(target);
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

    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getCost(){
        return 30;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_6_Level_Bonus();

        return 15 + ((int)(skillLevel/3));
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

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

            if(distance > range + buffAndDebuffManager.getTotalRangeModifier(caster)){
                return false;
            }

            if(distance<1){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        if(getCooldown(caster) > 0){
            return false;
        }


        return energy.getCurrentEnergy(caster) >= getCost();
    }

    public int returnWhichItem(Player player){

        if(energy.getCurrentEnergy(player)<getCost()){
            return 8;
        }

        return 0;
    }

}
