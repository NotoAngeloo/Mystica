package me.angeloo.mystica.Components.Abilities.Elementalist;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
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
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class ConjuringForce {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final PvpManager pvpManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ConjuringForce(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        pvpManager = main.getPvpManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
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

        abilityReadyInMap.put(player.getUniqueId(), 26);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void execute(Player player){

        Location spawnStart = player.getLocation().clone();
        spawnStart.subtract(0, 1, 0);

        ArmorStand spawnTexture = spawnStart.getWorld().spawn(spawnStart, ArmorStand.class);
        spawnTexture.setInvisible(true);
        spawnTexture.setGravity(false);
        spawnTexture.setCollidable(false);
        spawnTexture.setInvulnerable(true);
        spawnTexture.setMarker(true);

        EntityEquipment entityEquipment2 = spawnTexture.getEquipment();

        ItemStack spawnItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta2 = spawnItem.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(13);
        spawnItem.setItemMeta(meta2);
        assert entityEquipment2 != null;
        entityEquipment2.setHelmet(spawnItem);

        double skillLevel = profileManager.getAnyProfile(player).getStats().getLevel();
        double buffAmount = 5 * skillLevel;

        new BukkitRunnable(){
            int ran = 0;
            final Set<Player> affected = new HashSet<>();

            final Location loc = spawnTexture.getLocation();
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

                loc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);
                loc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc2, 1, 0, 0, 0, 0);

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


                BoundingBox hitBox = new BoundingBox(
                        spawnTexture.getLocation().getX() - 4,
                        spawnTexture.getLocation().getY() - 2,
                        spawnTexture.getLocation().getZ() - 4,
                        spawnTexture.getLocation().getX() + 4,
                        spawnTexture.getLocation().getY() + 4,
                        spawnTexture.getLocation().getZ() + 4
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

                    buffAndDebuffManager.getConjuringForceBuff().applyConjuringForceBuff(thisPlayer, buffAmount);


                }


                if(ran >=140){
                    cancelTask();
                }

                ran ++;
            }

            private void cancelTask(){
                this.cancel();
                spawnTexture.remove();

                for(Player thisPlayer : affected){
                    buffAndDebuffManager.getConjuringForceBuff().removeConjuringForceBuff(thisPlayer);
                }

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
