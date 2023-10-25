package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RazorWind {

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
    private final StarVolley starVolley;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public RazorWind(Mystica main, AbilityManager manager, RangerAbilities rangerAbilities){
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
        starVolley = rangerAbilities.getStarVolley();
    }

    public void use(Player player){
        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
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

        abilityReadyInMap.put(player.getUniqueId(), 16);
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

            }
        }.runTaskTimer(main, 0,20);
    }

    private void execute(Player player){

        boolean scout = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("scout");

        LivingEntity target = targetManager.getPlayerTarget(player);

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_4_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_4_Level_Bonus();
        double skillDamage = 4;

        abilityManager.getRangerAbilities().setCasting(player, true);
        player.setWalkSpeed(.06f);

        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    this.cancel();
                    abilityManager.getRangerAbilities().setCasting(player, false);
                    player.setWalkSpeed(.2f);
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                double percent = ((double) count / 20) * 100;

                abilityManager.getRangerAbilities().setCastBar(player, percent);

                if(count >=20){
                    this.cancel();
                    abilityManager.getRangerAbilities().setCasting(player, false);
                    player.setWalkSpeed(.2f);
                    startLaunchTask();
                }

                count ++;
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void startLaunchTask(){

                Location start = player.getLocation();

                start.subtract(0, 1, 0);
                ArmorStand armorStand = start.getWorld().spawn(start, ArmorStand.class);
                armorStand.setInvisible(true);
                armorStand.setGravity(false);
                armorStand.setCollidable(false);
                armorStand.setInvulnerable(true);
                armorStand.setMarker(true);

                EntityEquipment entityEquipment = armorStand.getEquipment();

                ItemStack razor = new ItemStack(Material.ARROW);
                ItemMeta meta = razor.getItemMeta();
                assert meta != null;
                meta.setCustomModelData(5);
                razor.setItemMeta(meta);
                assert entityEquipment != null;
                entityEquipment.setHelmet(razor);

                new BukkitRunnable(){
                    boolean toFrom = false;
                    Location newTargetWasLoc = targetWasLoc.clone();
                    Location playerWasLoc = player.getLocation().clone();
                    Vector initialDirection;
                    int angle = 0;
                    @Override
                    public void run(){

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation();
                            targetLoc = targetLoc.subtract(0,1,0);
                            newTargetWasLoc = targetLoc.clone();
                        }

                        if(targetStillValid(player)){
                            Location playerLoc = player.getLocation();
                            playerLoc = playerLoc.subtract(0,1,0);
                            playerWasLoc = playerLoc.clone();
                        }

                        Location current = armorStand.getLocation();

                        if (!sameWorld(current, targetWasLoc)) {
                            cancelTask();
                            return;
                        }

                        Vector direction;
                        double distance;

                        if(!toFrom){
                            direction = targetWasLoc.toVector().subtract(current.toVector());
                            distance = current.distance(targetWasLoc);
                        }
                        else{
                            direction = playerWasLoc.toVector().subtract(current.toVector());
                            distance = current.distance(playerWasLoc);
                        }

                        double distanceThisTick = Math.min(distance, .6);
                        current.add(direction.normalize().multiply(distanceThisTick));


                        if (initialDirection == null) {
                            initialDirection = playerWasLoc.getDirection().setY(0).normalize();
                        }

                        Vector rotation = initialDirection.clone();
                        double radians = Math.toRadians(angle);
                        rotation.rotateAroundY(radians);
                        current.setDirection(rotation);

                        armorStand.teleport(current);

                        if(toFrom){
                            if (distance <= 1) {
                                cancelTask();
                            }
                        }

                        if(!toFrom){
                            if (distance <= 1) {

                                toFrom = true;

                                boolean crit = damageCalculator.checkIfCrit(player, subclassCritBonus(player));

                                if(scout && crit){
                                    starVolley.decreaseCooldown(player);
                                    buffAndDebuffManager.getHaste().applyHaste(player, 1, 2);
                                }

                                double damage = damageCalculator.calculateDamage(player, target, "Physical", skillDamage * skillLevel, crit);

                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                                changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                            }
                        }

                        angle += 60; // adjust the rotation speed here
                        if (angle >= 360) {
                            angle = 0;
                        }

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

        }.runTaskTimer(main, 0, 2);

    }

    private int subclassCritBonus(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("scout")){
            return 15;
        }

        return 0;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
