package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.*;
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

public class ShadowGrip {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ShadowGrip(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 15;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        targetManager.setTargetToNearestValid(player, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        double cost = 30;

        if(profileManager.getAnyProfile(player).getCurrentMana() < cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 6);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 6);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean blood = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("blood");

        double skillDamage = 15;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));
        
        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
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

        abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            boolean pulled = false;
            boolean going = true;
            @Override
            public void run(){

                if(!targetStillValid(player)){
                    cancelTask();
                    return;
                }

                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                Location playerLoc = player.getLocation();
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
                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);


                    if(blood){
                        aggroManager.setAsHighPriorityTarget(target, player);
                        if(target instanceof Player){
                            targetManager.setPlayerTarget((Player) target, player);
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
                abilityManager.setSkillRunning(player, false);
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

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
