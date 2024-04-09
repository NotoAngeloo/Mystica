package me.angeloo.mystica.Components.Abilities.Paladin;

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
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class DivineInfusion {

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

    public DivineInfusion(Mystica main, AbilityManager manager){
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

        double baseRange = 10;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }

            if(target instanceof Player){
                if(profileManager.getAnyProfile(target).getIfDead()){
                    target = player;
                }
            }

        }

        if(target == null){
            target = player;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player, target);

        abilityReadyInMap.put(player.getUniqueId(), 18);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 4);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 4);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player, LivingEntity target){

        Location start = target.getLocation().clone().add(0,4,0);

        Location end = target.getLocation().clone().subtract(0,.25,0);

        ArmorStand armorStand = player.getWorld().spawn(start.clone(), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(12);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);



        Set<Player> hitBySkill = new HashSet<>();

        double finalSkillDamage = getSkillDamage(player);
        new BukkitRunnable(){
            int count = 0;
            boolean down = true;
            @Override
            public void run(){

                if(down){
                    Location current = armorStand.getLocation();
                    Vector direction = end.toVector().subtract(current.toVector());;
                    double distance = current.distance(end);
                    double distanceThisTick = Math.min(distance, .75);

                    if(distanceThisTick!=0){
                        current.add(direction.normalize().multiply(distanceThisTick));
                    }

                    armorStand.teleport(current);

                    if (distance <= 1) {
                        down = false;
                    }

                }

                if(!down){

                    if(count%20==0){
                        double increment = (2 * Math.PI) / 16; // angle between particles

                        for (int i = 0; i < 16; i++) {
                            double angle = i * increment;
                            double x = end.getX() + (4 * Math.cos(angle));
                            double y = end.getY() + 1;
                            double z = end.getZ() + (4 * Math.sin(angle));
                            Location loc = new Location(end.getWorld(), x, y, z);
                            player.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
                        }

                        Set<Player> hitByThisTick = new HashSet<>();

                        BoundingBox hitBox = new BoundingBox(
                                end.getX() - 4,
                                end.getY() - 2,
                                end.getZ() - 4,
                                end.getX() + 4,
                                end.getY() + 6,
                                end.getZ() + 4
                        );

                        for(Entity entity : player.getWorld().getNearbyEntities(hitBox)){

                            if(!(entity instanceof LivingEntity)){
                                continue;
                            }

                            if(entity instanceof ArmorStand){
                                continue;
                            }

                            LivingEntity livingEntity = (LivingEntity) entity;

                            boolean crit = damageCalculator.checkIfCrit(player, 0);
                            double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

                            if(livingEntity instanceof Player){

                                Player thisPlayer = (Player) livingEntity;

                                if(pvpManager.pvpLogic(player, (Player) entity)){
                                    changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                }
                                else{
                                    hitByThisTick.add(thisPlayer);
                                }
                                continue;
                            }

                            if(pveChecker.pveLogic(livingEntity)){
                                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                            }

                        }

                        for(Player thisPlayer : hitByThisTick){

                            if(hitBySkill.contains(thisPlayer)){
                                continue;
                            }

                            hitBySkill.add(thisPlayer);

                            double amount = (profileManager.getAnyProfile(thisPlayer).getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(thisPlayer)) * .05;
                            buffAndDebuffManager.getSpeedUp().applySpeedUp(thisPlayer, .3f);
                            buffAndDebuffManager.getGenericShield().applyOrAddShield(thisPlayer,amount);
                            removeBuffsLater(thisPlayer, amount);
                        }

                    }


                    count++;
                }

                if(count>=20*6){
                    cancelTask();
                }
            }

            private void removeBuffsLater(Player thisPlayer, double shield){
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        buffAndDebuffManager.getSpeedUp().removeSpeedUp(thisPlayer);
                        buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(thisPlayer, shield);
                    }
                }.runTaskLater(main, 20*3);
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main,0,1);

    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_4_Level_Bonus();
        return 15 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 10;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
