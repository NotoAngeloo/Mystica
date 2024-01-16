package me.angeloo.mystica.Components.Abilities.Warrior;

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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TempestRage {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public TempestRage(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player) {

        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (abilityReadyInMap.get(player.getUniqueId()) > 0) {
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_5_Level_Bonus();

        int cooldown = 10;

        cooldown = cooldown - ((int)(skillLevel/15));

        abilityReadyInMap.put(player.getUniqueId(), cooldown);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    cooldownDisplayer.displayCooldown(player, 3);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 3);
            }
        }.runTaskTimer(main, 0, 20);
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

        ItemStack item = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(5);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        double skillDamage = 20;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_3_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        int ticks = 6;

        double finalSkillDamage = skillDamage / ticks;
        new BukkitRunnable(){
            int timeRan = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){

                if(!player.isOnline()){
                    cancelTask();
                }

                if(profileManager.getAnyProfile(player).getIfDead()){
                    cancelTask();
                    return;
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

                if(timeRan%20==0){
                    BoundingBox hitBox = new BoundingBox(
                            player.getLocation().getX() - 5,
                            player.getLocation().getY() - 2,
                            player.getLocation().getZ() - 5,
                            player.getLocation().getX() + 5,
                            player.getLocation().getY() + 6,
                            player.getLocation().getZ() + 5
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

                        boolean crit = damageCalculator.checkIfCrit(player, 0);
                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

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
                }

                if(timeRan >= 20*ticks){
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
