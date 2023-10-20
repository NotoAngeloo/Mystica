package me.angeloo.mystica.Components.Abilities.Ranger;

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

public class ShadowCrows {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ShadowCrows(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
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

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean tamer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("animal tamer");

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location start = player.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = start.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack crow = new ItemStack(Material.ARROW);
        ItemMeta meta = crow.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(2);
        crow.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(crow);

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_2_Level_Bonus();
        double skillDamage = 2;

        new BukkitRunnable(){
            Location targetWasLoc = target.getLocation().clone();
            @Override
            public void run(){

                if(!player.isOnline()){
                    this.cancel();
                    armorStand.remove();
                }

                if(targetStillValid(target)){
                    Location targetLoc = target.getLocation();
                    targetLoc = targetLoc.subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location current = armorStand.getLocation();
                Vector direction = targetWasLoc.toVector().subtract(current.toVector());

                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, 2);
                current.add(direction.normalize().multiply(distanceThisTick));
                armorStand.teleport(current);

                if (distance <= 3) {
                    this.cancel();
                    crowTask();

                    if(tamer){
                        buffAndDebuffManager.getShadowCrowsDebuff().applyDebuff(target, 15);
                    }

                }
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }


            private void crowTask(){
                new BukkitRunnable(){
                    Location targetWasLoc = target.getLocation().clone();
                    Vector initialDirection;
                    int count = 0;
                    int angle = 0;
                    @Override
                    public void run(){

                        if(targetStillValid(target)){
                            Location targetLoc = target.getLocation();
                            targetWasLoc = targetLoc.clone();
                        }

                        if (initialDirection == null) {
                            initialDirection = targetWasLoc.getDirection().setY(0).normalize();
                        }

                        Vector direction = initialDirection.clone();
                        double radians = Math.toRadians(angle);
                        direction.rotateAroundY(radians);
                        targetWasLoc.setDirection(direction);
                        armorStand.teleport(targetWasLoc);


                        if(!targetStillValid(target)){
                            this.cancel();
                            armorStand.remove();
                            return;
                        }

                        if(count%20 == 0){

                            boolean crit = damageCalculator.checkIfCrit(player, subclassCritBonus(player));
                            double damage = damageCalculator.calculateDamage(player, target, "Physical", skillDamage * skillLevel, crit);

                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                            changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                        }

                        if(count >= 150){
                            this.cancel();
                            armorStand.remove();
                        }

                        angle -= 10; // adjust the rotation speed here
                        if (angle <= -360) {
                            angle = 0;
                        }

                        count++;

                    }
                }.runTaskTimer(main, 0, 1);
            }

        }.runTaskTimer(main, 0, 1);

    }

    private int subclassCritBonus(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("animal tamer")){
            return 15;
        }

        return 0;
    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
