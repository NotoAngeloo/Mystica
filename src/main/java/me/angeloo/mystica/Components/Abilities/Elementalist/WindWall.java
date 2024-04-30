package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WindWall {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public WindWall(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);

    }

    public void use(Player player) {

        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (getCooldown(player) > 0) {
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();

        int cooldown = 21;

        cooldown = cooldown - ((int)(skillLevel/3));

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), cooldown);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(player) <= 0) {
                    cooldownDisplayer.displayCooldown(player, 5);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 5);
            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(player.getUniqueId(), task);
    }

    private void execute(Player player){

        Location start = player.getLocation();

        ArmorStand armorStand = player.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack matrixItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = matrixItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(12);
        matrixItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(matrixItem);

        buffAndDebuffManager.getWindWallBuff().createAWindWall(player);

        new BukkitRunnable(){
            int timeRan = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                }

                if(!buffAndDebuffManager.getWindWallBuff().getIfWindWallActive(player)){
                    cancelTask();
                }

                if (initialDirection == null) {
                    initialDirection = player.getLocation().getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);

                Location playerLoc = player.getLocation().clone();
                playerLoc.setDirection(direction);

                armorStand.teleport(playerLoc);

                if(timeRan >= 200){
                    cancelTask();
                }

                timeRan++;

                angle += 20;
                if (angle >= 360) {
                    angle = 0;
                }
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
                buffAndDebuffManager.getWindWallBuff().removeWindwall(player);
            }

        }.runTaskTimer(main, 0, 1);

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
