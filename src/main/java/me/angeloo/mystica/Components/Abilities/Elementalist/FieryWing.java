package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
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

public class FieryWing {

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

    private final Map<UUID, Integer> inflameMap = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public FieryWing(Mystica main, AbilityManager manager){
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

        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 30);
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

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);


        ArmorStand spawnTexture = player.getWorld().spawn(start, ArmorStand.class);
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
        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            boolean spawned = false;
            int ran = 0;
            public void run(){

                if(player.isOnline()){
                    spawnTexture.teleport(player.getLocation().clone().subtract(0,1,0));
                }
                else{
                    cancelTask();
                }

                if(ran >= 10 && !spawned){

                    //abilityManager.setSkillRunning(player, false);
                    spawned = true;

                    ArmorStand armorStand = player.getWorld().spawn(player.getLocation().clone().subtract(0,1,0), ArmorStand.class);
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


                            player.getWorld().spawnParticle(Particle.FLAME, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                            if (distance <= 1) {

                                addInflame(player);

                                cancelTask();

                                boolean crit = damageCalculator.checkIfCrit(player, 0);
                                double damage = damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit);

                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                                changeResourceHandler.subtractHealthFromEntity(target, damage, player);


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

    public int getInflame(Player player){

        if(!inflameMap.containsKey(player.getUniqueId())){
            inflameMap.put(player.getUniqueId(), 0);
        }

        return inflameMap.get(player.getUniqueId());
    }

    public void addInflame(Player player){

        boolean pyromancer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("pyromancer");

        if(!pyromancer){
            return;
        }

        int stacks = getInflame(player);

        stacks ++;

        if(stacks >=4){
            abilityReadyInMap.put(player.getUniqueId(), 0);
            removeInflame(player);
            return;
        }

        inflameMap.put(player.getUniqueId(), stacks);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
    }


    public void removeInflame(Player player){
        inflameMap.put(player.getUniqueId(), 0);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
    }

    public double getCost() {
        return 20;
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        return 60 + ((int)(skillLevel/3));
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }



}
