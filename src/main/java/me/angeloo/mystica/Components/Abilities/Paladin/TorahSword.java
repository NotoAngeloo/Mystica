package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Components.Abilities.PaladinAbilities;
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
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TorahSword {

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

    private final Decision decision;
    private final Judgement judgement;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public TorahSword(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
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
        decision = paladinAbilities.getDecision();
        judgement = paladinAbilities.getJudgement();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 10;
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

        double cost = 5;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean dawn = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("dawn");

        LivingEntity target = targetManager.getPlayerTarget(player);

        Vector direction = player.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

        Location start = target.getLocation().clone();
        start.add(0, 5, 0);

        ArmorStand sword1 = player.getWorld().spawn(start, ArmorStand.class);
        sword1.setInvisible(true);
        sword1.setGravity(false);
        sword1.setCollidable(false);
        sword1.setInvulnerable(true);
        sword1.setMarker(true);

        EntityEquipment entityEquipment = sword1.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(3);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        sword1.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        Location start2 = start.clone().add(crossProduct.multiply(2));
        ArmorStand sword2 = player.getWorld().spawn(start2, ArmorStand.class);
        sword2.setInvisible(true);
        sword2.setGravity(false);
        sword2.setCollidable(false);
        sword2.setInvulnerable(true);
        sword2.setMarker(true);

        EntityEquipment entityEquipment2 = sword2.getEquipment();
        assert entityEquipment2 != null;
        entityEquipment2.setItemInMainHand(item);

        sword2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(-30)));

        Location start3 = start.clone().subtract(crossProduct.multiply(2));
        ArmorStand sword3 = player.getWorld().spawn(start3, ArmorStand.class);
        sword3.setInvisible(true);
        sword3.setGravity(false);
        sword3.setCollidable(false);
        sword3.setInvulnerable(true);
        sword3.setMarker(true);

        EntityEquipment entityEquipment3 = sword3.getEquipment();
        assert entityEquipment3 != null;
        entityEquipment3.setItemInMainHand(item);

        sword3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(30)));

        Location end = target.getLocation().clone().subtract(0,2,0);

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();
        double skillDamage = 7;

        skillDamage = skillDamage + ((int)(skillLevel/10));

        int critValue = 0;

        if(dawn){
            critValue = 15;
        }


        int finalCritValue = critValue;
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){

                Location current1 = sword1.getLocation();

                Vector direction1 = end.toVector().subtract(current1.toVector());
                double distance1 = current1.distance(end);
                double distanceThisTick1 = Math.min(distance1, 1);
                current1.add(direction1.normalize().multiply(distanceThisTick1));

                if(distance1 > 1){
                    sword1.teleport(current1);
                }

                Location current2 = sword2.getLocation();

                Vector direction2 = end.toVector().subtract(current2.toVector());
                double distance2 = current2.distance(end);
                double distanceThisTick2 = Math.min(distance2, 1);
                current2.add(direction2.normalize().multiply(distanceThisTick2));

                if(distance2 > 1 && count>=3){
                    sword2.teleport(current2);
                }

                Location current3 = sword3.getLocation();

                Vector direction3 = end.toVector().subtract(current3.toVector());
                double distance3 = current3.distance(end);
                double distanceThisTick3 = Math.min(distance3, 1);
                current3.add(direction3.normalize().multiply(distanceThisTick3));

                if(distance3 > 1 && count>=5){
                    sword3.teleport(current3);
                }


                if (distance1 <= 1) {

                    boolean crit = damageCalculator.checkIfCrit(player, finalCritValue);

                    if(crit&&dawn){
                        judgement.resetCooldown(player);
                        decision.applyDecision(player);
                    }

                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                }

                if (distance2 <= 1) {

                    boolean crit = damageCalculator.checkIfCrit(player, finalCritValue);

                    if(crit&&dawn){
                        judgement.resetCooldown(player);
                        decision.applyDecision(player);
                    }

                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                }

                if (distance3 <= 1) {

                    cancelTask();

                    boolean crit = damageCalculator.checkIfCrit(player, finalCritValue);

                    if(crit&&dawn){
                        judgement.resetCooldown(player);
                        decision.applyDecision(player);
                    }

                    double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                }

                count++;
            }

            private void cancelTask() {
                this.cancel();
                sword1.remove();
                sword2.remove();
                sword3.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
