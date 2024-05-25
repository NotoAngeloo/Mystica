package me.angeloo.mystica.Components.Abilities.Paladin;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.PveChecker;
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

public class SacredAegis {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SacredAegis(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }


        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){

            if(!(target instanceof Player)){
                target = player;
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > getRange(player)){
                return;
            }

            if (profileManager.getAnyProfile(target).getIfDead()) {
                target = player;
            }

            if(pvpManager.pvpLogic(player, (Player) target)){
                target = player;
            }

        }

        if(target == null){
            target = player;
        }

        if(getCooldown(player) > 0){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player, (Player) target);


        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), getSkillCooldown(player));
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(player) <= 0){
                    cooldownDisplayer.displayCooldown(player, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 6);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(player.getUniqueId(), task);

    }


    private double getRange(Player player){
        double baseRange = 12;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        return baseRange + extraRange;
    }

    private void execute(Player player, Player target){

        Location start = target.getLocation().clone();
        Vector direction = target.getLocation().getDirection().setY(0).normalize();
        Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(7);
        item.setItemMeta(meta);

        Location s1Spawn = start.clone().add(direction.clone().multiply(1)).setDirection(direction);
        ArmorStand shield = player.getWorld().spawn(s1Spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield.setInvisible(true);
        shield.setGravity(false);
        shield.setCollidable(false);
        shield.setInvulnerable(true);
        shield.setMarker(true);
        EntityEquipment entityEquipment = shield.getEquipment();
        assert entityEquipment != null;
        entityEquipment.setItemInMainHand(item);
        shield.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield.teleport(s1Spawn);

        Location s2spawn = start.clone().subtract(direction.clone().multiply(1)).setDirection(direction.clone().multiply(-1));
        ArmorStand shield2 = player.getWorld().spawn(s2spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield2.setInvisible(true);
        shield2.setGravity(false);
        shield2.setCollidable(false);
        shield2.setInvulnerable(true);
        shield2.setMarker(true);
        EntityEquipment entityEquipment2 = shield2.getEquipment();
        assert entityEquipment2 != null;
        entityEquipment2.setItemInMainHand(item);
        shield2.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield2.teleport(s2spawn);

        Location s3spawn = start.clone().add(crossProduct.clone().multiply(1)).setDirection(crossProduct);
        ArmorStand shield3 = player.getWorld().spawn(s3spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield3.setInvisible(true);
        shield3.setGravity(false);
        shield3.setCollidable(false);
        shield3.setInvulnerable(true);
        shield3.setMarker(true);
        EntityEquipment entityEquipment3 = shield3.getEquipment();
        assert entityEquipment3 != null;
        entityEquipment3.setItemInMainHand(item);
        shield3.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield3.teleport(s3spawn);

        Location shield4spawn = start.clone().subtract(crossProduct.clone().multiply(1)).setDirection(crossProduct.clone().multiply(-1));
        ArmorStand shield4 = player.getWorld().spawn(shield4spawn.clone().subtract(0,5,0), ArmorStand.class);
        shield4.setInvisible(true);
        shield4.setGravity(false);
        shield4.setCollidable(false);
        shield4.setInvulnerable(true);
        shield4.setMarker(true);
        EntityEquipment entityEquipment4 = shield4.getEquipment();
        assert entityEquipment4 != null;
        entityEquipment4.setItemInMainHand(item);
        shield4.setRightArmPose(new EulerAngle(Math.toRadians(0), Math.toRadians(0), Math.toRadians(0)));
        shield4.teleport(shield4spawn);

        buffAndDebuffManager.getImmune().applyImmune(target, 100);

        new BukkitRunnable(){
            Vector initialDirection;
            double angle = 0;
            int count = 0;
            @Override
            public void run(){

                if (!target.isOnline()) {
                    cancelTask();
                    return;
                }


                if (initialDirection == null) {
                    initialDirection = target.getLocation().getDirection().setY(0).normalize();
                }

                Location center = target.getLocation();
                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);
                Vector crossProduct = direction.clone().crossProduct(new Vector(0,1,0)).normalize();

                Location s1 = center.clone().add(direction.clone().multiply(1)).setDirection(direction);
                shield.teleport(s1);
                Location s2 = center.clone().subtract(direction.clone().multiply(1)).setDirection(direction.clone().multiply(-1));
                shield2.teleport(s2);
                Location s3 = center.clone().add(crossProduct.clone().multiply(1)).setDirection(crossProduct);
                shield3.teleport(s3);
                Location s4 = center.clone().subtract(crossProduct.clone().multiply(1)).setDirection(crossProduct.clone().multiply(-1));
                shield4.teleport(s4);

                if(count>=20*5){
                    cancelTask();
                }

                angle+=5;
                count++;
            }

            private void cancelTask(){
                this.cancel();
                shield.remove();
                shield2.remove();
                shield3.remove();
                shield4.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public double getCost(){
        return 20;
    }

    public int getSkillCooldown(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();
        return 120 - ((int) skillLevel/3);
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
