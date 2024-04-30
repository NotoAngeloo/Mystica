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
import org.bukkit.scheduler.BukkitTask;
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
    private final CooldownDisplayer cooldownDisplayer;

    private final Stealth stealth;
    private final Combo combo;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public Assault(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        targetManager = main.getTargetManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        stealth = assassinAbilities.getStealth();
        combo = assassinAbilities.getCombo();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 4;

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


        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 4);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 1);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 1);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }

    private void execute(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);
        Location start = player.getLocation().clone();

        ArmorStand stand = player.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
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
        stand.setRightArmPose(new EulerAngle(Math.toRadians(180), Math.toRadians(0), Math.toRadians(0)));
        stand.teleport(start.clone().add(0,.5,0));

        double skillDamage = getSkillDamage(player);
        //abilityManager.setSkillRunning(player, true);
        new BukkitRunnable(){
            int angle = 180;
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

                double distance = current.distance(targetLoc);
                double distanceThisTick = Math.min(distance, .5);

                if(distanceThisTick>1){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                player.teleport(current);
                stand.teleport(current.clone().add(0, 0.5, 0));
                angle+=15;
                stand.setRightArmPose(new EulerAngle(Math.toRadians(angle), Math.toRadians(0), Math.toRadians(0)));

                if(angle>=360){

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = damageCalculator.calculateDamage(player, target, "Physical", skillDamage, crit);

                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);

                    combo.addComboPoint(player);

                    stealth.stealthBonusCheck(player, target);

                    cancelTask();
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
                //abilityManager.setSkillRunning(player, false);
                stand.remove();
            }

        }.runTaskTimer(main, 0, 1);
    }

    public double getSkillDamage(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_1_Level_Bonus();
        return 30 + ((int)(skillLevel/3));
    }

    public double getCost(){
        return 5;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }



}
