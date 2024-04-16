package me.angeloo.mystica.Components.Abilities.Mystic;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EvilSpirit {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final ShieldAbilityManaDisplayer shieldAbilityManaDisplayer;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final AbilityManager abilityManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final Map<UUID, Integer> chaosShards = new HashMap<>();
    private final Map<UUID, Boolean> isEvilSpirit = new HashMap<>();

    public EvilSpirit(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        shieldAbilityManaDisplayer = new ShieldAbilityManaDisplayer(main, manager);
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        abilityManager = manager;
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){

        if(getChaosShards(player)<6){
            return;
        }

        Block block = player.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);
        execute(player);
    }

    private void execute(Player player){

        //hide player and display animation. when over, put hat on head
        int castTime = 25;

        buffAndDebuffManager.getImmobile().applyImmobile(player, castTime);

        Location spawnStart = player.getLocation().clone();
        spawnStart.subtract(0, 1, 0);

        Location current = player.getLocation().clone();

        abilityManager.setCasting(player, true);

        buffAndDebuffManager.getHidden().hidePlayer(player, false);

        new BukkitRunnable(){
            final Location loc = current.clone();
            Vector initialDirection;
            int ran = 0;
            int angle = 0;
            double height = 0;
            final double radius = 4;
            @Override
            public void run(){

                if (initialDirection == null) {
                    initialDirection = current.getDirection().setY(0).normalize();
                }

                Vector rotation = initialDirection.clone();
                double radians = Math.toRadians(angle);
                rotation.rotateAroundY(radians);
                current.setDirection(rotation);



                double x = loc.getX() + rotation.getX() * radius;
                double z = loc.getZ() + rotation.getZ() * radius;

                double x2 = loc.getX() - rotation.getX() * radius;
                double z2 = loc.getZ() - rotation.getZ() * radius;

                Location particleLoc = new Location(loc.getWorld(), x, loc.getY() + height, z);
                Location particleLoc2 = new Location(loc.getWorld(), x2, loc.getY() + height, z2);

                player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc, 1, 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.GLOW_SQUID_INK, particleLoc2, 1, 0, 0, 0, 0);


                height += .1;



                double percent = ((double) ran / castTime) * 100;

                abilityManager.setCastBar(player, percent);

                if(ran >= castTime){
                    this.cancel();
                    abilityManager.setCasting(player, false);
                    abilityManager.setCastBar(player, 0);
                    evilSpiritTime(player);
                }

                angle += 5;
                if (angle >=360) {
                    angle = 0;
                }

                ran++;
            }
        }.runTaskTimer(main, 0, 1);

    }

    private void evilSpiritTime(Player player){

        if(!player.isOnline()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        isEvilSpirit.put(player.getUniqueId(), true);
        removeShards(player);

        ItemStack spirit = new ItemStack(Material.SPECTRAL_ARROW);
        ItemMeta meta2 = spirit.getItemMeta();
        assert meta2 != null;
        meta2.setCustomModelData(5);
        spirit.setItemMeta(meta2);

        player.getInventory().setItemInOffHand(spirit);

        new BukkitRunnable(){
            int count = 0;

            @Override
            public void run(){

                if(count >= 30){
                    this.cancel();
                    isEvilSpirit.put(player.getUniqueId(), false);
                    buffAndDebuffManager.getHidden().unhidePlayer(player);
                }

                count++;
            }
        }.runTaskTimer(main, 0, 10);

    }

    public boolean getIfEvilSpirit(Player player){
        return isEvilSpirit.getOrDefault(player.getUniqueId(), false);
    }

    public int getChaosShards(Player player){
        return chaosShards.getOrDefault(player.getUniqueId(), 0);
    }

    public void addChaosShard(Player player, int added){

        if(!chaosShards.containsKey(player.getUniqueId())){
            chaosShards.put(player.getUniqueId(), 0);
        }

        int current = chaosShards.get(player.getUniqueId());

        current = current + added;

        if(current > 6){
            current = 6;
        }

        chaosShards.put(player.getUniqueId(), current);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        shieldAbilityManaDisplayer.displayPlayerHealthPlusInfo(player);
    }

    public void removeShards(Player player){
        chaosShards.put(player.getUniqueId(), 0);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
    }

    public int returnWhichItem(Player player){

        if(getChaosShards(player) >= 6){
            return 1;
        }

        return 0;
    }

    public double getCost(){
        return 40;
    }

}
