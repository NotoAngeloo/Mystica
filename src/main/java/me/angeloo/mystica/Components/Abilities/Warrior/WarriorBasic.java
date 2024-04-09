package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarriorBasic {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> basicStageMap = new HashMap<>();
    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBasicStageTaskMap = new HashMap<>();

    public WarriorBasic(Mystica main, AbilityManager manager){
        this.main = main;

        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void useBasic(Player player){

        if(!basicStageMap.containsKey(player.getUniqueId())){
            basicStageMap.put(player.getUniqueId(), 1);
        }

        if(getIfBasicRunning(player)){
            return;
        }

        executeBasic(player);

    }

    private void tryToRemoveBasicStage(Player player){

        if(removeBasicStageTaskMap.containsKey(player.getUniqueId())){
            removeBasicStageTaskMap.get(player.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){
                basicStageMap.remove(player.getUniqueId());
            }
        }.runTaskLater(main, 50);

        removeBasicStageTaskMap.put(player.getUniqueId(), task);

    }

    private void executeBasic(Player player){

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                tryToRemoveBasicStage(player);
                switch (basicStageMap.get(player.getUniqueId())) {
                    case 1: {
                        basicStage1(player, 2);
                        break;
                    }
                    case 2: {
                        basicStage2(player, 3);
                        break;
                    }
                    case 3: {
                        basicStage1(player, 4);
                        break;
                    }
                    case 4: {
                        basicStage2(player, 5);
                        break;
                    }
                    case 5: {
                        basicStage4(player);
                        break;
                    }
                }
                combatManager.startCombatTimer(player);
            }
        }.runTaskTimer(main, 0, 10);
        basicRunning.put(player.getUniqueId(), task);


        combatManager.startCombatTimer(player);
    }

    private void basicStage4(Player player){
        basicStageMap.put(player.getUniqueId(), 1);
    }

    private void basicStage1(Player player, int newStage){

        basicStageMap.put(player.getUniqueId(), newStage);

        Location start = player.getLocation().clone().subtract(0,3,0);

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();
        start.add(direction.multiply(4));
        start.add(crossProduct.multiply(3));

        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack basicItem = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = basicItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        basicItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(basicItem);


        Location loc = player.getLocation().clone().add(direction.multiply(1.25));


        BoundingBox hitBox = new BoundingBox(
                loc.getX() - 3,
                loc.getY() - 2,
                loc.getZ() - 3,
                loc.getX() + 3,
                loc.getY() + 4,
                loc.getZ() + 3
        );

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(player);
        LivingEntity firstHit = null;

        boolean targetHit = false;


        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(entity == player){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) entity)){
                    continue;
                }
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            if(!(entity instanceof Player)){
                if(!pveChecker.pveLogic(livingEntity)){
                    continue;
                }
            }

            if(firstHit == null){
                firstHit = livingEntity;
            }

            if(target != null){
                if(livingEntity == target){
                    targetHit = true;
                    targetToHit = livingEntity;
                    break;
                }
            }
        }

        if(!targetHit && firstHit!= null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);

            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

        }
        else{
            stopBasicRunning(player);
        }

        new BukkitRunnable(){
            double traveled = 0;
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                }

                Vector direction = player.getLocation().getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location current = player.getLocation().clone();
                current.add(direction.multiply(4));
                current.add(crossProduct.multiply(3));
                current.subtract(crossProduct.multiply(traveled));

                armorStand.teleport(current);

                if(traveled>=2){
                    cancelTask();
                }

                if(count>100){
                    cancelTask();
                }

                count++;
                traveled +=.3;

            }

            private void cancelTask(){
                armorStand.remove();
                this.cancel();


            }

        }.runTaskTimer(main, 0, 1);

    }

    private void basicStage2(Player player, int newStage){

        basicStageMap.put(player.getUniqueId(), newStage);

        Location start = player.getLocation().clone().subtract(0,3,0);

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();
        start.add(direction.multiply(4));
        start.subtract(crossProduct.multiply(3));

        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack basicItem = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = basicItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(2);
        basicItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(basicItem);


        Location loc = player.getLocation().clone().add(direction.multiply(1.25));


        BoundingBox hitBox = new BoundingBox(
                loc.getX() - 3,
                loc.getY() - 2,
                loc.getZ() - 3,
                loc.getX() + 3,
                loc.getY() + 4,
                loc.getZ() + 3
        );

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(player);
        LivingEntity firstHit = null;

        boolean targetHit = false;



        for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

            if(entity == player){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) entity)){
                    continue;
                }
            }

            if(entity instanceof ArmorStand){
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            if(!(entity instanceof Player)){
                if(!pveChecker.pveLogic(livingEntity)){
                    continue;
                }
            }

            if(firstHit == null){
                firstHit = livingEntity;
            }

            if(target != null){
                if(livingEntity == target){
                    targetHit = true;
                    targetToHit = livingEntity;
                    break;
                }
            }
        }

        if(!targetHit && firstHit!= null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);

            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);
        }
        else{
            stopBasicRunning(player);
        }

        new BukkitRunnable(){
            double traveled = 0;
            int count = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                }

                Vector direction = player.getLocation().getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location current = player.getLocation().clone();
                current.add(direction.multiply(4));
                current.subtract(crossProduct.multiply(3));
                current.add(crossProduct.multiply(traveled));

                armorStand.teleport(current);

                //player.getWorld().spawnParticle(Particle.SPELL_WITCH, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                if(traveled>=2){
                    cancelTask();
                }

                if(count>100){
                    cancelTask();
                }

                traveled +=.3;
                count++;

            }

            private void cancelTask(){
                armorStand.remove();
                this.cancel();


            }

        }.runTaskTimer(main, 0, 1);

    }

    private boolean getIfBasicRunning(Player player){
        return basicRunning.containsKey(player.getUniqueId());
    }

    public void stopBasicRunning(Player player){
        if(basicRunning.containsKey(player.getUniqueId())){
            basicRunning.get(player.getUniqueId()).cancel();
            basicRunning.remove(player.getUniqueId());
        }
    }

    public double getSkillDamage(Player player){
        double level = profileManager.getAnyProfile(player).getStats().getLevel();
        return 14 + ((int)(level/10));
    }

}
