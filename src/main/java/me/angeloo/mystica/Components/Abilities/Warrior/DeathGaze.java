package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.Components.Abilities.WarriorAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.UltimateStatusChageEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
    private final Rage rage;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public DeathGaze(Mystica main, AbilityManager manager, WarriorAbilities warriorAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        rage = warriorAbilities.getRage();
    }

    private final double range = 20;

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }

        targetManager.setTargetToNearestValid(caster, range + buffAndDebuffManager.getTotalRangeModifier(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), getSkillCooldown());
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getPlayerCooldown(caster) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = getPlayerCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);
                abilityReadyInMap.put(caster.getUniqueId(), cooldown);

                if(caster instanceof Player){
                    Bukkit.getServer().getPluginManager().callEvent(new UltimateStatusChageEvent((Player) caster));
                }

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        LivingEntity target = targetManager.getPlayerTarget(caster);



        //abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            boolean valid = false;
            Location targetWasLoc = target.getLocation().clone();
            final List<ArmorStand> armorStands = new ArrayList<>();
            int ran = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                        return;
                    }
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    cancelTask();
                    return;
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation().clone();
                    targetWasLoc = targetLoc.clone();
                }

                Location playerLoc = caster.getLocation().clone();

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

                        if(distanceThisTick!=0){
                            current.add(direction.normalize().multiply(distanceThisTick));
                        }

                        current.setDirection(direction);

                        ArmorStand armorStand = caster.getWorld().spawn(current.clone().subtract(0,5,0), ArmorStand.class);
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
                    boolean crit = damageCalculator.checkIfCrit(caster, 0);
                    double damage = (damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit));
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);
                    rage.addRageToEntity(caster, 10);

                }

                if(ran<10 && ran!=0) {
                    Vector direction = targetWasLoc.toVector().subtract(playerLoc.toVector());
                    current.setDirection(direction);
                    double distanceThisTick = Math.min(distance, .45);

                    for (ArmorStand thisStand : armorStands) {

                        if(distanceThisTick!=0){
                            current.add(direction.normalize().multiply(distanceThisTick));
                        }

                        thisStand.teleport(current);

                    }
                }

                if(ran>=10){

                    double dPull = playerLoc.distance(targetWasLoc);
                    if(valid){
                        //pull
                        buffAndDebuffManager.getPulled().applyPull(target);

                        if(dPull <= 1){
                            cancelTask();
                            buffAndDebuffManager.getPulled().removePull(target);

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

                        if(targetStillValid(caster)){
                            caster.teleport(caster.getLocation().add(direction.normalize().multiply(distanceThisTick)));
                        }
                    }

                }

                if(ran>=60){
                    cancelTask();
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
                //abilityManager.setSkillRunning(player, false);
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

    public int getPlayerCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public int getSkillCooldown(){
        return 25;
    }

    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 25 + ((int)(skillLevel/3));
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
        }

        if(target == null){
            return false;
        }

        return getPlayerCooldown(caster) <= 0;
    }


}
