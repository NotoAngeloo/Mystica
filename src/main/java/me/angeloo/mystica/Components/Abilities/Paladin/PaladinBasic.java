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

    public void useBasic(LivingEntity caster){

        if(!basicStageMap.containsKey(caster.getUniqueId())){
            basicStageMap.put(caster.getUniqueId(), 1);
        }

        if(getIfBasicRunning(caster)){
            return;
        }

        executeBasic(caster);

    }

    private void tryToRemoveBasicStage(LivingEntity caster){

        if(removeBasicStageTaskMap.containsKey(caster.getUniqueId())){
            removeBasicStageTaskMap.get(caster.getUniqueId()).cancel();
        }

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getIfBasicInterrupt(caster)){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                if(targetManager.getPlayerTarget(caster) != null){
                    if(profileManager.getAnyProfile(targetManager.getPlayerTarget(caster)).getIfDead()){
                        this.cancel();
                        stopBasicRunning(caster);
                        return;
                    }
                }


                basicStageMap.remove(caster.getUniqueId());
            }
        }.runTaskLater(main, 50);

        removeBasicStageTaskMap.put(caster.getUniqueId(), task);

    }

    private double getRange(LivingEntity caster){
        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(caster);
        return baseRange + extraRange;
    }

    private void healTarget(LivingEntity caster, LivingEntity target){

        boolean crit = damageCalculator.checkIfCrit(caster, 0);

        double healPower = 1;

        double healAmount = damageCalculator.calculateHealing(caster, healPower, crit);

        if(justiceMark.markProc(caster, target)){
            markHealInstead(caster, healAmount);
            return;
        }

        changeResourceHandler.addHealthToEntity(target, healAmount, caster);

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

    private void markHealInstead(LivingEntity caster, double healAmount){

        List<LivingEntity> affected = justiceMark.getMarkedTargets(caster);

        for(LivingEntity thisPlayer : affected){
            changeResourceHandler.addHealthToEntity(thisPlayer, healAmount, caster);

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

    private void executeBasic(LivingEntity caster){

        String subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(subclass.equalsIgnoreCase("divine")){

                    if(targetManager.getPlayerTarget(caster) == null){
                        healTarget(caster, caster);
                        return;
                    }

                    if(targetManager.getPlayerTarget(caster) instanceof Player){
                        Player target = (Player) targetManager.getPlayerTarget(caster);

                        if(!pvpManager.pvpLogic(caster, target)){

                            Location playerLocation = caster.getLocation();
                            Location targetLocation = target.getLocation();

                            double distance = playerLocation.distance(targetLocation);

                            if (distance > getRange(caster)) {
                                stopBasicRunning(caster);
                                return;
                            }

                            healTarget(caster, target);
                            return;
                        }
                    }

                    if(!(targetManager.getPlayerTarget(caster) instanceof Player)){

                        LivingEntity target = targetManager.getPlayerTarget(caster);

                        if(!pveChecker.pveLogic(target)){

                            Location playerLocation = caster.getLocation();
                            Location targetLocation = target.getLocation();

                            double distance = playerLocation.distance(targetLocation);

                            if (distance > getRange(caster)) {
                                stopBasicRunning(caster);
                                return;
                            }

                            healTarget(caster, target);
                            return;
                        }
                    }

                }

                //check heal instead here

                tryToRemoveBasicStage(caster);
                switch (getStage(caster)){
                    case 1:{
                        basicStage1(caster);
                        break;
                    }
                    case 2:{
                        basicStage2(caster);
                        break;
                    }
                    case 3:{
                        basicStage3(caster);
                        break;
                    }
                    case 4:{
                        basicStage4(caster);
                        break;
                    }
                    case 5:{
                        basicStage5(caster);
                        break;

                    }


                }

                combatManager.startCombatTimer(caster);
            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(caster.getUniqueId(), task);



    }

    private void basicStage5(LivingEntity caster){
        basicStageMap.put(caster.getUniqueId(), 1);
    }

    private void basicStage1(LivingEntity caster){

        basicStageMap.put(caster.getUniqueId(), 2);

        Location start = caster.getLocation().clone().subtract(0,3,0);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        start.add(direction.multiply(3));
        start.add(0,6,0);
        direction.rotateAroundY(45);
        start.setDirection(direction);

        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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


        Location loc = caster.getLocation().clone().add(direction.multiply(1.25));

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
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;

        boolean targetHit = false;


        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
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
            targetManager.setPlayerTarget(caster, targetToHit);

            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster)
                    + representative.getAdditionalBonusFromBuff(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster);

            gloryOfPaladins.procGlory(caster, targetToHit);

        }
        else{
            stopBasicRunning(caster);
        }

        new BukkitRunnable(){
            double traveled = 0;
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                    }
                }

                Vector direction = caster.getLocation().getDirection().setY(0).normalize();

                Location current = caster.getLocation().clone();
                current.add(direction.multiply(3));
                current.add(0,2,0);
                current.subtract(0,traveled,0);
                direction.rotateAroundY(45);
                current.setDirection(direction);
                armorStand.teleport(current);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

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

    private void basicStage2(LivingEntity caster){

        basicStageMap.put(caster.getUniqueId(), 3);

        Location start = caster.getLocation().clone().subtract(0,3,0);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();
        start.add(direction.multiply(4));
        start.subtract(crossProduct.multiply(3));

        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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

        Location loc = caster.getLocation().clone().add(direction.multiply(1.25));

        BoundingBox hitBox = new BoundingBox(
                loc.getX() - 3,
                loc.getY() - 2,
                loc.getZ() - 3,
                loc.getX() + 3,
                loc.getY() + 4,
                loc.getZ() + 3
        );

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;

        boolean targetHit = false;



        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
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
            targetManager.setPlayerTarget(caster, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster)
                    + representative.getAdditionalBonusFromBuff(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster);

            gloryOfPaladins.procGlory(caster, targetToHit);

        }
        else{
            stopBasicRunning(caster);
        }

        new BukkitRunnable(){
            double traveled = 0;
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                    }
                }

                Vector direction = caster.getLocation().getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location current = caster.getLocation().clone();
                current.add(direction.multiply(4));
                current.subtract(crossProduct.multiply(3));
                current.add(crossProduct.multiply(traveled));

                armorStand.teleport(current);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

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

    private void basicStage3(LivingEntity caster){

        basicStageMap.put(caster.getUniqueId(), 4);

        Location start = caster.getLocation().clone().subtract(0,3,0);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        start.add(direction.multiply(3));
        direction.rotateAroundY(-45);
        start.setDirection(direction);

        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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


        Location loc = caster.getLocation().clone().add(direction.multiply(1.25));

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
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;

        boolean targetHit = false;


        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
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
            targetManager.setPlayerTarget(caster, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster)
                    + representative.getAdditionalBonusFromBuff(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster);

            gloryOfPaladins.procGlory(caster, targetToHit);

        }
        else{
            stopBasicRunning(caster);
        }

        new BukkitRunnable(){
            double traveled = 0;
            int count = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                    }
                }

                Vector direction = caster.getLocation().getDirection().setY(0).normalize();

                Location current = caster.getLocation().clone();
                current.add(direction.multiply(3));
                current.add(0,traveled,0);
                direction.rotateAroundY(-45);
                current.setDirection(direction);
                armorStand.teleport(current);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

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

    private void basicStage4(LivingEntity caster){

        basicStageMap.put(caster.getUniqueId(), 5);

        Location start = caster.getLocation().clone().subtract(0,3,0);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();
        start.add(direction.multiply(4));
        start.add(crossProduct.multiply(3));

        ArmorStand armorStand = caster.getWorld().spawn(start, ArmorStand.class);
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


        Location loc = caster.getLocation().clone().add(direction.multiply(1.25));


        BoundingBox hitBox = new BoundingBox(
                loc.getX() - 3,
                loc.getY() - 2,
                loc.getZ() - 3,
                loc.getX() + 3,
                loc.getY() + 4,
                loc.getZ() + 3
        );

        LivingEntity targetToHit = null;
        LivingEntity target = targetManager.getPlayerTarget(caster);
        LivingEntity firstHit = null;

        boolean targetHit = false;


        for (Entity entity : caster.getWorld().getNearbyEntities(hitBox)) {

            if(entity == caster){
                continue;
            }

            if(entity.isDead()){
                continue;
            }

            if(!(entity instanceof LivingEntity)){
                continue;
            }

            if(entity instanceof Player){
                if(!pvpManager.pvpLogic(caster, (Player) entity)){
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
            targetManager.setPlayerTarget(caster, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster)
                    + representative.getAdditionalBonusFromBuff(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster);

            gloryOfPaladins.procGlory(caster, targetToHit);
        }
        else{
            stopBasicRunning(caster);
        }

        new BukkitRunnable(){
            double traveled = 0;
            @Override
            public void run(){

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        cancelTask();
                    }
                }

                Vector direction = caster.getLocation().getDirection().setY(0).normalize();
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location current = caster.getLocation().clone();
                current.add(direction.multiply(4));
                current.add(crossProduct.multiply(3));
                current.subtract(crossProduct.multiply(traveled));

                armorStand.teleport(current);

                caster.getWorld().spawnParticle(Particle.WAX_OFF, current.clone().add(0,1,0), 1, 0, 0, 0, 0);

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

    private boolean getIfBasicRunning(LivingEntity caster){
        return basicRunning.containsKey(caster.getUniqueId());
    }

    public void stopBasicRunning(LivingEntity caster){
        if(basicRunning.containsKey(caster.getUniqueId())){
            basicRunning.get(caster.getUniqueId()).cancel();
            basicRunning.remove(caster.getUniqueId());
        }
    }

    public double getSkillDamage(LivingEntity caster){
        double level = profileManager.getAnyProfile(caster).getStats().getLevel();
        return 10 + ((int)(level/3));
    }

    private int getStage(LivingEntity caster){
        return basicStageMap.getOrDefault(caster.getUniqueId(), 1);
    }

}
