package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.Abilities.ShadowKnightAbilities;
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

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
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

        double skillDamage = 2;
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


        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            boolean going = true;
            Location targetWasLoc = target.getLocation().clone();
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

                if(going){
                    Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                    double distance = current.distance(targetWasLoc);
                    double distanceThisTick = Math.min(distance, .5);
                    current.add(direction.normalize().multiply(distanceThisTick));
                    current.setDirection(direction);
                    armorStand.teleport(current);

                    if (distance <= 1) {

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                        changeResourceHandler.subtractHealthFromEntity(target, damage, player);


                        if(blood){
                            aggroManager.setAsHighPriorityTarget(target, player);
                        }

                        //also check and pull creature
                        pullTarget();
                        going = false;
                    }
                }
                else{

                    if(!player.isOnline()){
                        cancelTask();
                        return;
                    }


                    Vector direction = player.getLocation().toVector().subtract(current.toVector());
                    double distance = current.distance(player.getLocation());
                    double distanceThisTick = Math.min(distance, .9);
                    current.add(direction.normalize().multiply(distanceThisTick));

                    Vector opposite = direction.clone().multiply(-1);
                    current.setDirection(opposite);
                    armorStand.teleport(current);

                    if(targetStillValid(target) && profileManager.getAnyProfile(target).getIsMovable()){
                        //also check if its movable
                        target.teleport(armorStand);

                    }

                    if(distance <=1){
                        cancelTask();
                    }

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
