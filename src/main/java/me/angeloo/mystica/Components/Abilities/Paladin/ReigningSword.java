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

public class ReigningSword {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Decision decision;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ReigningSword(Mystica main, AbilityManager manager, PaladinAbilities paladinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        decision = paladinAbilities.getDecision();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
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
                    cooldownDisplayer.displayCooldown(player, 3);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 3);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean templar = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("templar");

        Vector direction = player.getLocation().getDirection().setY(0).normalize();

        Location start = player.getLocation().clone().add(direction.multiply(1));
        start.setDirection(direction);

        ArmorStand sword = player.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
        sword.setInvisible(true);
        sword.setGravity(false);
        sword.setCollidable(false);
        sword.setInvulnerable(true);
        sword.setMarker(true);

        EntityEquipment entityEquipment = sword.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);

        sword.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));

        sword.teleport(start);

        Set<LivingEntity> hitBySkill = new HashSet<>();

        double skillDamage = 25;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        double shield = (profileManager.getAnyProfile(player).getTotalHealth()+ buffAndDebuffManager.getHealthBuffAmount(player)) * 0.1;

        if(templar){
            shield = shield * 1.2;
        }

        buffAndDebuffManager.getGenericShield().applyOrAddShield(player, shield);

        double finalShield = shield;
        new BukkitRunnable(){
            @Override
            public void run(){
                buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(player, finalShield);
            }
        }.runTaskLater(main, 20*5);

        abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                    return;
                }

                if (initialDirection == null) {
                    initialDirection = player.getLocation().getDirection().setY(0).normalize();
                }

                Location center = player.getLocation();

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location loc = center.clone().add(direction.clone().multiply(1)).setDirection(direction);
                sword.teleport(loc);

                if(angle <= 360){
                    player.teleport(player.getLocation().setDirection(direction));
                }

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - 5,
                        center.getY() - 2,
                        center.getZ() - 5,
                        center.getX() + 5,
                        center.getY() + 5,
                        center.getZ() + 5
                );

                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                    if(entity == player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(hitBySkill.contains(livingEntity)){
                        continue;
                    }

                    hitBySkill.add(livingEntity);

                    double bonus = 1;

                    if(templar){
                        bonus = 2.2;
                    }

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage
                            * bonus * decisionMultiplier(player), crit));


                    //pvp logic
                    if(entity instanceof Player){
                        if(pvpManager.pvpLogic(player, (Player) entity)){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                        }
                        continue;
                    }

                    if(pveChecker.pveLogic(livingEntity)){
                        Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                        changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                    }

                }

                if(angle >= 460){
                    cancelTask();
                }

                angle+=30;
            }

            private void cancelTask(){
                this.cancel();
                sword.remove();
                decision.removeDecision(player);
                abilityManager.setSkillRunning(player, false);
            }

        }.runTaskTimer(main, 0, 1);

    }

    private double decisionMultiplier(Player player){

        if(decision.getDecision(player)){
            return 1.8;
        }

        return 1;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
