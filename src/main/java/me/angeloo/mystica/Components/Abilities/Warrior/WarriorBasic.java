package me.angeloo.mystica.Components.Abilities.Warrior;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Abilities.WarriorAbilities;
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
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final ChangeResourceHandler changeResourceHandler;
    private final Rage rage;

    private final Map<UUID, Integer> basicStageMap = new HashMap<>();
    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBasicStageTaskMap = new HashMap<>();

    public WarriorBasic(Mystica main, AbilityManager manager, WarriorAbilities warriorAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        changeResourceHandler = main.getChangeResourceHandler();
        rage = warriorAbilities.getRage();
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
                basicStageMap.remove(caster.getUniqueId());
            }
        }.runTaskLater(main, 50);

        removeBasicStageTaskMap.put(caster.getUniqueId(), task);

    }

    private void executeBasic(LivingEntity caster){

        basicRunning.put(caster.getUniqueId(), null);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(buffAndDebuffManager.getIfBasicInterrupt(caster)){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }



                if(profileManager.getAnyProfile(caster).getIfDead()){
                    this.cancel();
                    stopBasicRunning(caster);
                    return;
                }

                if(targetManager.getPlayerTarget(caster) != null){
                    if(profileManager.getAnyProfile(targetManager.getPlayerTarget(caster)).getIfDead() || targetManager.getPlayerTarget(caster).isDead()){
                        this.cancel();
                        stopBasicRunning(caster);
                        return;
                    }
                }

                if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
                }

                tryToRemoveBasicStage(caster);
                switch (getStage(caster)) {
                    case 1: {
                        basicStage1(caster, 2);
                        break;
                    }
                    case 2: {
                        basicStage2(caster, 3);
                        break;
                    }
                    case 3: {
                        basicStage1(caster, 4);
                        break;
                    }
                    case 4: {
                        basicStage2(caster, 5);
                        break;
                    }
                    case 5: {
                        basicStage4(caster);
                        break;
                    }
                }
                combatManager.startCombatTimer(caster);
            }
        }.runTaskTimer(main, 0, 10);
        basicRunning.put(caster.getUniqueId(), task);


        combatManager.startCombatTimer(caster);
    }

    private void basicStage4(LivingEntity caster){
        basicStageMap.put(caster.getUniqueId(), 1);
    }

    private void basicStage1(LivingEntity caster, int newStage){

        basicStageMap.put(caster.getUniqueId(), newStage);

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

        ItemStack basicItem = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = basicItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(1);
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
            if(caster instanceof Player){
                targetManager.setPlayerTarget((Player)caster, targetToHit);
            }
            else{
                fakePlayerTargetManager.setFakePlayerTarget(caster, targetToHit);
            }

            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);
            rage.addRageToEntity(caster, 10);

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

    private void basicStage2(LivingEntity caster, int newStage){

        basicStageMap.put(caster.getUniqueId(), newStage);

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

        ItemStack basicItem = new ItemStack(Material.NETHER_WART);
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
            if(caster instanceof Player){
                targetManager.setPlayerTarget((Player)caster, targetToHit);
            }
            else{
                fakePlayerTargetManager.setFakePlayerTarget(caster, targetToHit);
            }

            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);
            rage.addRageToEntity(caster, 10);
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
        return 14 + ((int)(level/3));
    }

    private int getStage(LivingEntity caster){
        return basicStageMap.getOrDefault(caster.getUniqueId(), 1);
    }

}
