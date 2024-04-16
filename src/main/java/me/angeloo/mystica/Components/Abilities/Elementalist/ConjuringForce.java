package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class ConjuringForce {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;
    private final PvpManager pvpManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ConjuringForce(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        pvpManager = main.getPvpManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){
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

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 26);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(player) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(player.getUniqueId(), task);
    }

    private void execute(Player player){

        Location start = player.getLocation().clone();


        new BukkitRunnable(){
            int ran = 0;
            final Set<Player> affected = new HashSet<>();

            final Location loc = start.clone();
            double height = 0;
            boolean up = true;
            final double radius = 4;
            double angle = 0;
            Vector initialDirection;
            @Override
            public void run(){

                Set<Player> hitBySkill = new HashSet<>();

                if(initialDirection == null) {
                    initialDirection = loc.getDirection().setY(0).normalize();
                    initialDirection.rotateAroundY(Math.toRadians(-45));
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);

                direction.rotateAroundY(radians);

                double x = loc.getX() + direction.getX() * radius;
                double z = loc.getZ() + direction.getZ() * radius;

                double x2 = loc.getX() - direction.getX() * radius;
                double z2 = loc.getZ() - direction.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                player.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc2, 1, 0, 0, 0, 0);

                if(up){
                    height += .1;
                }
                else{
                    height -= .1;
                }

                angle += 5;

                if(height >= 4){
                    up = false;
                }

                if(height < 0){
                    up = true;
                }

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x3 = start.getX() + (4 * Math.cos(angle));
                    double y3 = start.getY() + 1;
                    double z3 = start.getZ() + (4 * Math.sin(angle));
                    Location loc = new Location(start.getWorld(), x3, y3, z3);
                    player.getWorld().spawnParticle(Particle.SPELL_WITCH, loc, 1,0, 0, 0, 0);
                }


                BoundingBox hitBox = new BoundingBox(
                        start.getX() - 4,
                        start.getY() - 2,
                        start.getZ() - 4,
                        start.getX() + 4,
                        start.getY() + 4,
                        start.getZ() + 4
                );


                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof Player)){
                        continue;
                    }

                    Player thisPlayer = (Player) entity;

                    if(pvpManager.pvpLogic(player, thisPlayer)){
                        continue;
                    }

                    hitBySkill.add(player);
                    affected.add(player);
                }

                for(Player thisPlayer : affected){
                    if(!hitBySkill.contains(thisPlayer)){
                        affected.remove(thisPlayer);
                        buffAndDebuffManager.getConjuringForceBuff().removeConjuringForceBuff(thisPlayer);
                        continue;
                    }

                    buffAndDebuffManager.getConjuringForceBuff().applyConjuringForceBuff(thisPlayer, getBuffAmount(player));


                }


                if(ran >=140){
                    cancelTask();
                }

                ran ++;
            }

            private void cancelTask(){
                this.cancel();

                for(Player thisPlayer : affected){
                    buffAndDebuffManager.getConjuringForceBuff().removeConjuringForceBuff(thisPlayer);
                }

            }

        }.runTaskTimer(main, 0, 1);
    }

    public double getCost(){
        return 20;
    }

    public double getBuffAmount(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        return 5 + skillLevel;
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
