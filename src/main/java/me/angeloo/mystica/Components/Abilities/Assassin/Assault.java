package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
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

public class Assault {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Combo combo;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Assault(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        abilityManager = manager;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combo = assassinAbilities.getCombo();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 6;

        targetManager.setTargetToNearestValid(player, baseRange);

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

            if(distance > baseRange){
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

        abilityReadyInMap.put(player.getUniqueId(), 4);
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

        LivingEntity target = targetManager.getPlayerTarget(player);
        Location start = player.getLocation().clone();
        Location up = start.clone().add(0,3,0);

        double skillDamage = 4;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        abilityManager.setSkillRunning(player, true);
        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int angle = 180;
            Location current2;
            ArmorStand stand;
            boolean land = false;
            boolean going = true;
            @Override
            public void run(){

                if(!player.isOnline() || buffAndDebuffManager.getIfInterrupt(player)){
                    cancelTask();
                    return;
                }

                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                Location current = player.getLocation();
                Location targetLoc = target.getLocation().clone();
                Vector direction = targetLoc.toVector().subtract(current.toVector());
                direction.setY(0);

                if(!going){
                    double distance = current.distance(start);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector downDir = start.toVector().subtract(current.toVector());
                    current.add(downDir.normalize().multiply(distanceThisTick));

                    if(distance<=1){
                        land = true;
                    }

                }

                if(going){
                    double distance = current.distance(up);
                    double distanceThisTick = Math.min(distance, .5);
                    Vector upDir = up.toVector().subtract(current.toVector());
                    current.add(upDir.normalize().multiply(distanceThisTick));

                    if(distance<=1){
                        going=false;

                        current2 = current.clone();

                        stand = player.getWorld().spawn(current2.clone().subtract(0,10,0), ArmorStand.class);
                        stand.setInvisible(true);
                        stand.setGravity(false);
                        stand.setCollidable(false);
                        stand.setInvulnerable(true);
                        stand.setMarker(true);
                        ItemStack item = new ItemStack(Material.SLIME_BALL);
                        ItemMeta meta = item.getItemMeta();
                        assert meta != null;
                        meta.setCustomModelData(1);
                        item.setItemMeta(meta);
                        EntityEquipment entityEquipment = stand.getEquipment();
                        assert entityEquipment != null;
                        entityEquipment.setItemInMainHand(item);
                        stand.setRightArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));
                        stand.teleport(current2);
                    }
                }

                current.setDirection(direction);

                if(!land){
                    player.teleport(current);
                }


                if(stand != null){
                    current2 = current.clone().add(0,0.5,0);
                    stand.teleport(current2);
                    angle+=15;
                    stand.setRightArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));

                    if(angle>=360){

                        if(targetStillValid(target)){
                            boolean crit = damageCalculator.checkIfCrit(player, 0);
                            double damage = damageCalculator.calculateDamage(player, target, "Physical", finalSkillDamage, crit);

                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                            changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                            combo.addComboPoint(player);
                        }

                        cancelTask();
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

            private void cancelTask(){
                this.cancel();
                abilityManager.setSkillRunning(player, false);

                if(stand== null){
                    return;
                }

                stand.remove();
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
