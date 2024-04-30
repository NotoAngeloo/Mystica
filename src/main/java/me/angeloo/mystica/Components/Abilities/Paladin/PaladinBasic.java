package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaladinBasic {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Representative representative;
    private final JusticeMark justiceMark;
    private final GloryOfPaladins gloryOfPaladins;

    private final Map<UUID, Integer> basicStageMap = new HashMap<>();
    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBasicStageTaskMap = new HashMap<>();

    public PaladinBasic(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        representative = paladinAbilities.getRepresentative();
        gloryOfPaladins = paladinAbilities.getGloryOfPaladins();
        justiceMark = paladinAbilities.getJusticeMark();
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

                if(buffAndDebuffManager.getIfBasicInterrupt(player)){
                    this.cancel();
                    stopBasicRunning(player);
                    return;
                }

                basicStageMap.remove(player.getUniqueId());
            }
        }.runTaskLater(main, 50);

        removeBasicStageTaskMap.put(player.getUniqueId(), task);

    }

    private double getRange(Player player){
        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void healTarget(Player player, Player target){

        boolean crit = damageCalculator.checkIfCrit(player, 0);

        double healPower = 1;

        double healAmount = damageCalculator.calculateHealing(player, healPower, crit);

        if(justiceMark.markProc(player, target)){
            markHealInstead(player, healAmount);
            return;
        }

        changeResourceHandler.addHealthToEntity(target, healAmount, player);

        Location center = target.getLocation().clone().add(0,1,0);

        double increment = (2 * Math.PI) / 16; // angle between particles

        for (int i = 0; i < 16; i++) {
            double angle = i * increment;
            double x = center.getX() + (1 * Math.cos(angle));
            double z = center.getZ() + (1 * Math.sin(angle));
            Location loc = new Location(center.getWorld(), x, (center.getY()), z);

            target.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
        }

    }

    private void markHealInstead(Player player, double healAmount){

        List<LivingEntity> affected = justiceMark.getMarkedTargets(player);

        for(LivingEntity thisPlayer : affected){
            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, player);

            Location center = thisPlayer.getLocation().clone().add(0,1,0);

            double increment = (2 * Math.PI) / 16; // angle between particles

            for (int i = 0; i < 16; i++) {
                double angle = i * increment;
                double x = center.getX() + (1 * Math.cos(angle));
                double z = center.getZ() + (1 * Math.sin(angle));
                Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                thisPlayer.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
            }
        }


    }

    private void executeBasic(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(subclass.equalsIgnoreCase("divine")){

                    if(targetManager.getPlayerTarget(player) == null){
                        healTarget(player, player);
                        return;
                    }

                    if(targetManager.getPlayerTarget(player) instanceof Player){
                        Player target = (Player) targetManager.getPlayerTarget(player);

                        if(!pvpManager.pvpLogic(player, target)){

                            Location playerLocation = player.getLocation();
                            Location targetLocation = target.getLocation();

                            double distance = playerLocation.distance(targetLocation);

                            if (distance > getRange(player)) {
                                stopBasicRunning(player);
                                return;
                            }

                            healTarget(player, target);
                            return;
                        }
                    }

                }

                //check heal instead here

                tryToRemoveBasicStage(player);
                switch (basicStageMap.get(player.getUniqueId())){
                    case 1:{
                        basicStage1(player);
                        break;
                    }
                    case 2:{
                        basicStage2(player);
                        break;
                    }
                    case 3:{
                        basicStage3(player);
                        break;
                    }
                    case 4:{
                        basicStage4(player);
                        break;
                    }
                    case 5:{
                        basicStage5(player);
                        break;

                    }


                }

                combatManager.startCombatTimer(player);
            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(player.getUniqueId(), task);



    }

    private void basicStage5(Player player){
        basicStageMap.put(player.getUniqueId(), 1);
    }

    private void basicStage1(Player player){

        basicStageMap.put(player.getUniqueId(), 2);

        Location start = player.getLocation().clone().subtract(0,3,0);

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        start.add(direction.multiply(3));
        start.add(0,6,0);
        direction.rotateAroundY(45);
        start.setDirection(direction);

        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack basicItem = new ItemStack(Material.SUGAR);
        ItemMeta meta = basicItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        basicItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(basicItem);


        Location loc = player.getLocation().clone().add(direction.multiply(1.25));

        //player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1, 0, 0, 0, 0);

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

        if(!targetHit && firstHit != null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);

            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player)
                    + representative.getAdditionalBonusFromBuff(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

            gloryOfPaladins.procGlory(player, targetToHit);

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

                Location current = player.getLocation().clone();
                current.add(direction.multiply(3));
                current.add(0,2,0);
                current.subtract(0,traveled,0);
                direction.rotateAroundY(45);
                current.setDirection(direction);
                armorStand.teleport(current);

                player.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                if(traveled>=3){
                    cancelTask();
                }

                if(count>100){
                    cancelTask();
                }

                traveled +=.5;
                count++;

            }

            private void cancelTask(){
                armorStand.remove();
                this.cancel();


            }

        }.runTaskTimer(main, 0, 1);
    }

    private void basicStage2(Player player){

        basicStageMap.put(player.getUniqueId(), 3);

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

        ItemStack basicItem = new ItemStack(Material.SUGAR);
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
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player)
                    + representative.getAdditionalBonusFromBuff(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

            gloryOfPaladins.procGlory(player, targetToHit);

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

                player.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                if(traveled>=2){
                    cancelTask();
                }

                if(count>100){
                    cancelTask();
                }

                count++;
                traveled +=.4;

            }

            private void cancelTask(){
                armorStand.remove();
                this.cancel();


            }

        }.runTaskTimer(main, 0, 2);

    }

    private void basicStage3(Player player){

        basicStageMap.put(player.getUniqueId(), 4);

        Location start = player.getLocation().clone().subtract(0,3,0);

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        start.add(direction.multiply(3));
        direction.rotateAroundY(-45);
        start.setDirection(direction);

        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack basicItem = new ItemStack(Material.SUGAR);
        ItemMeta meta = basicItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
        basicItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(basicItem);


        Location loc = player.getLocation().clone().add(direction.multiply(1.25));

        //player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, loc, 1, 0, 0, 0, 0);

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

        if(!targetHit && firstHit != null){
            targetToHit = firstHit;
        }

        if(targetToHit != null){
            targetManager.setPlayerTarget(player, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(player, 0);
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player)
                    + representative.getAdditionalBonusFromBuff(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

            gloryOfPaladins.procGlory(player, targetToHit);

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

                Location current = player.getLocation().clone();
                current.add(direction.multiply(3));
                current.add(0,traveled,0);
                direction.rotateAroundY(-45);
                current.setDirection(direction);
                armorStand.teleport(current);

                player.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                if(traveled>=2){
                    cancelTask();
                }

                if(count>100){
                    cancelTask();
                }

                count++;
                traveled +=.4;

            }

            private void cancelTask(){
                armorStand.remove();
                this.cancel();

            }

        }.runTaskTimer(main, 0, 1);

    }

    private void basicStage4(Player player){

        basicStageMap.put(player.getUniqueId(), 5);

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

        ItemStack basicItem = new ItemStack(Material.SUGAR);
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
            double damage = damageCalculator.calculateDamage(player, targetToHit, "Physical", getSkillDamage(player)
                    + representative.getAdditionalBonusFromBuff(player), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, player));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, player);

            gloryOfPaladins.procGlory(player, targetToHit);
        }
        else{
            stopBasicRunning(player);
        }

        new BukkitRunnable(){
            double traveled = 0;
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

                player.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

                if(traveled>=2){
                    cancelTask();
                }

                traveled +=.4;

            }

            private void cancelTask(){
                armorStand.remove();
                this.cancel();


            }

        }.runTaskTimer(main, 0, 2);

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
        return 10 + ((int)(level/3));
    }

}
