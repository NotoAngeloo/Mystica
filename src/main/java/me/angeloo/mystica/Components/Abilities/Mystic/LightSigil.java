package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
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
import org.bukkit.util.Vector;

import java.util.*;

public class LightSigil {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final PurifyingBlast purifyingBlast;

    public LightSigil(Mystica main, AbilityManager manager, MysticAbilities mysticAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        purifyingBlast = mysticAbilities.getPurifyingBlast();
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (abilityReadyInMap.get(player.getUniqueId()) > 0) {
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void execute(Player player){

        purifyingBlast.queueInstantCast(player);

        Location spawnStart = player.getLocation().clone();

        ArmorStand sigil = spawnStart.getWorld().spawn(spawnStart, ArmorStand.class);
        sigil.setInvisible(true);
        sigil.setGravity(false);
        sigil.setCollidable(false);
        sigil.setInvulnerable(true);
        sigil.setMarker(true);

        EntityEquipment entityEquipment = sigil.getEquipment();

        ItemStack item = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(12);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);

        Location current = sigil.getLocation();

        new BukkitRunnable(){
            Vector initialDirection;
            int angle = 0;
            int ran = 0;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = current.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                current.setDirection(rotation);

                sigil.teleport(current);

                if(ran%20==0){

                    BoundingBox hitBox = new BoundingBox(
                            current.getX() - 10,
                            current.getY() - 10,
                            current.getZ() - 10,
                            current.getX() + 10,
                            current.getY() + 10,
                            current.getZ() + 10
                    );

                    Set<Player> hitBySkill = new HashSet<>();

                    for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                        if(!(entity instanceof Player)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        Player thisPlayer = (Player) entity;

                        if (pvpManager.pvpLogic(player, thisPlayer)) {
                            continue;
                        }

                        hitBySkill.add(thisPlayer);

                    }

                    //default
                    Player healedPlayer = player;

                    double currentMissingHealth = 0;

                    for(Player thisPlayer : hitBySkill){

                        double maxHealth = profileManager.getAnyProfile(thisPlayer).getTotalHealth();
                        double currentHealth = profileManager.getAnyProfile(thisPlayer).getCurrentHealth();
                        double missingHealth = maxHealth - currentHealth;

                        if(currentMissingHealth>missingHealth){
                            currentMissingHealth = missingHealth;
                            healedPlayer = thisPlayer;
                        }

                    }

                    shootHealAtPlayer(player, sigil, healedPlayer);

                }

                if(ran>=20*15){
                    this.cancel();
                    sigil.remove();
                }

                angle += 5;
                ran++;
            }



        }.runTaskTimer(main, 0, 1);

    }

    private void shootHealAtPlayer(Player player, ArmorStand sigil, Player healedPlayer){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level_Bonus();

        Location start = sigil.getLocation();
        start.subtract(0, 1, 0);
        ArmorStand armorStand = start.getWorld().spawn(start, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack bolt = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta = bolt.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(1);

        bolt.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(bolt);

        new BukkitRunnable(){
            Location targetWasLoc = healedPlayer.getLocation().clone().subtract(0,1,0);
            @Override
            public void run(){

                if(targetStillValid(healedPlayer)){
                    Location targetLoc = healedPlayer.getLocation().clone().subtract(0,1,0);
                    targetWasLoc = targetLoc.clone();
                }

                Location current = armorStand.getLocation();

                if (!sameWorld(current, targetWasLoc)) {
                    cancelTask();
                    return;
                }

                Vector direction = targetWasLoc.toVector().subtract(current.toVector());
                double distance = current.distance(targetWasLoc);
                double distanceThisTick = Math.min(distance, .75);

                if(distanceThisTick !=0){
                    current.add(direction.normalize().multiply(distanceThisTick));
                }

                current.setDirection(direction);

                armorStand.teleport(current);

                if (distance <= 1) {

                    cancelTask();

                    double totalTargetHealth = profileManager.getAnyProfile(healedPlayer).getTotalHealth();
                    double yourMagic = profileManager.getAnyProfile(player).getTotalMagic();
                    boolean crit = damageCalculator.checkIfCrit(player, 0);

                    double healAmount = (totalTargetHealth + skillLevel) * .1;
                    healAmount = healAmount * (yourMagic/4);

                    if(subclass.equalsIgnoreCase("shepard")){
                        healAmount = healAmount * 1.2;
                    }

                    if(crit){
                        healAmount = healAmount * 1.5;
                    }

                    changeResourceHandler.addHealthToEntity(healedPlayer, healAmount, player);

                    Location center = healedPlayer.getLocation().clone().add(0,1,0);

                    double increment = (2 * Math.PI) / 16; // angle between particles

                    for (int i = 0; i < 16; i++) {
                        double angle = i * increment;
                        double x = center.getX() + (1 * Math.cos(angle));
                        double z = center.getZ() + (1 * Math.sin(angle));
                        Location loc = new Location(center.getWorld(), x, (center.getY()), z);

                        healedPlayer.getWorld().spawnParticle(Particle.WAX_OFF, loc, 1,0, 0, 0, 0);
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

            private boolean sameWorld(Location loc1, Location loc2) {
                return loc1.getWorld().equals(loc2.getWorld());
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
