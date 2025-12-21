package me.angeloo.mystica.Components.CombatSystem.Abilities.Paladin;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Representative representative;
    private final JusticeMark justiceMark;
    private final GloryOfPaladins gloryOfPaladins;

    private final Map<UUID, Integer> basicStageMap = new HashMap<>();
    private final Map<UUID, BukkitTask> basicRunning = new HashMap<>();

    private final Map<UUID, BukkitTask> removeBasicStageTaskMap = new HashMap<>();

    public PaladinBasic(Mystica main, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
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

                if(!statusEffectManager.canBasic(caster)){
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
        double extraRange = statusEffectManager.getAdditionalRange(caster);
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

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        basicRunning.put(caster.getUniqueId(), null);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(caster.isDead() || profileManager.getAnyProfile(caster).getIfDead()){
                    stopBasicRunning(caster);
                    this.cancel();
                    return;
                }

                if(targetManager.getPlayerTarget(caster) != null && targetManager.getPlayerTarget(caster).isDead()){
                    stopBasicRunning(caster);
                    this.cancel();
                    return;
                }


                if(subclass.equals(SubClass.Divine)){

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

                            if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
                            }

                            return;
                        }
                    }

                }

                if(MythicBukkit.inst().getAPIHelper().isMythicMob(caster.getUniqueId())){
                    AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).getEntity();
                    MythicBukkit.inst().getAPIHelper().getMythicMobInstance(caster).signalMob(abstractEntity, "basic");
                }

                tryToRemoveBasicStage(caster);

                switch (getStage(caster)) {
                    case 1 -> {
                        basicStage1(caster, 2);
                    }
                    case 2 -> {
                        basicStage1(caster, 3);
                    }
                    case 3 -> {
                        basicStage1(caster, 4);
                    }
                    case 4 -> {
                        basicStage1(caster, 5);
                    }
                    case 5 -> {
                        basicStage5(caster);

                    }
                }

            }
        }.runTaskTimer(main, 0, 15);
        basicRunning.put(caster.getUniqueId(), task);



    }

    private void basicStage5(LivingEntity caster){
        basicStageMap.put(caster.getUniqueId(), 1);
    }

    private void basicStage1(LivingEntity caster, int newStage){

        basicStageMap.put(caster.getUniqueId(), newStage);

        Vector direction = caster.getLocation().getDirection().setY(0).normalize();

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

            if(!(entity instanceof LivingEntity livingEntity)){
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
            if(caster instanceof Player){
                targetManager.setPlayerTarget((Player)caster, targetToHit);
            }

            //this is gone because paladin companions kept losing target and stopped taunting
            //fakePlayerTargetManager.setFakePlayerTarget(caster, targetToHit);


            boolean crit = damageCalculator.checkIfCrit(caster, 0);
            double damage = damageCalculator.calculateDamage(caster, targetToHit, "Physical", getSkillDamage(caster)
                    + representative.getAdditionalBonusFromBuff(caster), crit);

            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(targetToHit, caster));
            changeResourceHandler.subtractHealthFromEntity(targetToHit, damage, caster, crit);

            gloryOfPaladins.procGlory(caster, targetToHit);

        }

        Location startStand = caster.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(startStand.clone().subtract(0,10,0), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();


        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        if(newStage%2==0){
            meta.setCustomModelData(1);
        }
        else{
            meta.setCustomModelData(2);
        }


        item.setItemMeta(meta);

        assert entityEquipment != null;
        entityEquipment.setHelmet(item);
        armorStand.teleport(startStand);

        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){


                Location current = armorStand.getLocation();

                current.add(direction.normalize().multiply(.25));

                current.setDirection(direction);

                armorStand.teleport(current);


                if (count > 10) {
                    cancelTask();
                }


                count++;
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
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
        return 10 + ((int)(level/3));
    }

    private int getStage(LivingEntity caster){
        return basicStageMap.getOrDefault(caster.getUniqueId(), 1);
    }

}
