package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
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

import java.util.*;

public class DeathGaze {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DeathGaze(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
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

        abilityReadyInMap.put(player.getUniqueId(), 25);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayUltimateCooldown(player);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayUltimateCooldown(player);
            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        double skillDamage = 8;
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            boolean valid = false;
            Location targetWasLoc = target.getLocation().clone();
            final List<ArmorStand> armorStands = new ArrayList<>();
            int ran = 0;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    cancelTask();
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone();
                    targetWasLoc = targetLoc.clone();
                }

                Location playerLoc = player.getLocation().clone();

                if(!sameWorld(playerLoc, targetWasLoc)){
                    cancelTask();
                    return;
                }

                double distance = playerLoc.distance(targetWasLoc);
                Location current = playerLoc.clone();

                if(ran == 0){
                    for(double i = 0; i<distance;i+=.7){

                        Vector direction = targetWasLoc.toVector().subtract(playerLoc.toVector());
                        double distanceThisTick = Math.min(distance, .45);
                        current.add(direction.normalize().multiply(distanceThisTick));
                        current.setDirection(direction);

                        ArmorStand armorStand = player.getWorld().spawn(current.clone().subtract(0,5,0), ArmorStand.class);
                        armorStand.setInvisible(true);
                        armorStand.setGravity(false);
                        armorStand.setCollidable(false);
                        armorStand.setInvulnerable(true);
                        armorStand.setMarker(true);

                        EntityEquipment entityEquipment = armorStand.getEquipment();

                        ItemStack item = new ItemStack(Material.NETHER_WART);
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        meta.setCustomModelData(3);
                        item.setItemMeta(meta);
                        assert entityEquipment != null;
                        entityEquipment.setHelmet(item);

                        armorStand.teleport(current);

                        armorStands.add(armorStand);

                    }
                    //get the furthest one
                    ItemStack hook = new ItemStack(Material.NETHER_WART);
                    ItemMeta meta = hook.getItemMeta();
                    assert meta != null;
                    meta.setCustomModelData(4);
                    hook.setItemMeta(meta);
                    armorStands.sort(Comparator.comparingDouble(s -> s.getLocation().distance(playerLoc)));
                    ArmorStand stand = armorStands.get(armorStands.size()-1);
                    EntityEquipment equipment = stand.getEquipment();
                    assert equipment != null;
                    equipment.setHelmet(hook);

                    if(profileManager.getAnyProfile(target).getIsMovable()){
                        valid = true;
                    }
                    //and damage here
                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit));
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                }

                if(ran<10 && ran!=0) {
                    Vector direction = targetWasLoc.toVector().subtract(playerLoc.toVector());
                    current.setDirection(direction);
                    double distanceThisTick = Math.min(distance, .45);

                    for (ArmorStand thisStand : armorStands) {
                        current.add(direction.normalize().multiply(distanceThisTick));
                        thisStand.teleport(current);

                    }
                }

                if(ran>=10){

                    if(valid){
                        //pull
                        double dPull = playerLoc.distance(targetWasLoc);

                        if(dPull <= 1){
                            cancelTask();

                            if(targetStillValid(target)){
                                buffAndDebuffManager.getStun().applyStun(target, 20);
                            }

                            return;
                        }

                        Vector direction = playerLoc.toVector().subtract(targetWasLoc.toVector());

                        double distanceThisTick = Math.min(dPull, 1);

                        //do a wall check here
                        if(wallCheck(targetWasLoc, direction, distanceThisTick)){
                            cancelTask();
                            return;
                        }

                        for(ArmorStand stand : armorStands){

                            Location sLoc = stand.getLocation();
                            double dChain = sLoc.distance(playerLoc);

                            if(dChain<=1){
                                stand.remove();
                                continue;
                            }

                            double distanceThisChain = Math.min(distance, 1);
                            sLoc.add(direction.normalize().multiply(distanceThisChain));
                            stand.teleport(sLoc);

                        }

                        if(targetStillValid(target)){
                            target.teleport(target.getLocation().add(direction.normalize().multiply(distanceThisTick)));
                        }
                    }
                    else{
                        //self
                        double dPull = playerLoc.distance(targetWasLoc);

                        if(dPull <= 1){
                            cancelTask();

                            if(targetStillValid(target)){
                                buffAndDebuffManager.getStun().applyStun(target, 20);
                            }

                            return;
                        }

                        Vector direction = targetWasLoc.toVector().subtract(playerLoc.toVector());

                        double distanceThisTick = Math.min(dPull, 1);

                        if(wallCheck(targetWasLoc, direction, distanceThisTick)){
                            cancelTask();
                            return;
                        }

                        for(ArmorStand stand : armorStands){

                            Location sLoc = stand.getLocation();
                            double dChain = sLoc.distance(playerLoc);

                            if(dChain<=1){
                                stand.remove();
                            }

                        }

                        if(targetStillValid(player)){
                            player.teleport(player.getLocation().add(direction.normalize().multiply(distanceThisTick)));
                        }
                    }

                }

                ran++;
            }

            private boolean wallCheck(Location current, Vector direction, double distance){

                Location newLoc = current.clone().add(direction.normalize().multiply(distance));
                newLoc.add(0,1,0);
                Location newLoc2 = newLoc.clone().add(0,1,0);

                return !newLoc.getBlock().isPassable() || !newLoc2.getBlock().isPassable();
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
                removeArmorStands(armorStands);
            }

            private void removeArmorStands(List<ArmorStand> stands){

                if(armorStands.isEmpty()){
                    return;
                }

                for(ArmorStand stand : stands){
                    stand.remove();
                }
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
