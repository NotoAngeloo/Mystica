package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Pulled;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.CrowdControl.Stun;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.DamageType;
import me.angeloo.mystica.Utility.Logic.PveChecker;
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
import org.bukkit.util.Vector;

import java.util.*;

public class DeathGaze extends BaseAbility {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final Rage rage;
    private final CooldownManager cooldownManager;

    public DeathGaze(Mystica main, AbilityManager manager){
        super("death_gaze");
        this.main = main;
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        rage = manager.getRage();
        cooldownManager = main.getCooldownManager();
    }

    private final double range = 20;
    private final int baseCooldown = 25;
    private final int baseDamage = 40;

    @Override
    public boolean use(LivingEntity caster){


        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return false;
        }

        targetManager.setTargetToNearestValid(caster, range + statusEffectManager.getAdditionalRange(caster));

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        execute(caster);

        cooldownManager.start(caster.getUniqueId(), -1, (long) (baseCooldown * 1000));

        return true;
    }

    @Override
    public int cooldown() {
        return baseCooldown;
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

                if(!statusEffectManager.canCast(caster)){
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
                    double damage = (damageCalculator.calculateDamage(caster, target, DamageType.Physical, finalSkillDamage, crit, 0));
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
                        statusEffectManager.applyEffect(target, new Pulled(), null, null, caster);

                        if(dPull <= 1){
                            cancelTask();
                            statusEffectManager.removeEffect(target, "pull");

                            if(targetStillValid(target)){
                                statusEffectManager.applyEffect(target, new Stun(), 20, null, caster);
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
                                statusEffectManager.applyEffect(target, new Stun(), 20, null, caster);
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




    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getStats().getLevel();
        return baseDamage + ((int)(skillLevel/3));
    }


    @Override
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

            if(distance > range + statusEffectManager.getAdditionalRange(caster)){
                return false;
            }
        }

        if(target == null){
            return false;
        }

        return cooldownManager.isReady(caster.getUniqueId(), -1, statusEffectManager.getHastePercent(caster));
    }


}
