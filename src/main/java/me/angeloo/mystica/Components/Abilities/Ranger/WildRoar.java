package me.angeloo.mystica.Components.Abilities.Ranger;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WildRoar {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final PvpManager pvpManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public WildRoar(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        pvpManager = main.getPvpManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        double cost = 20;

        if(profileManager.getAnyProfile(player).getCurrentMana()<cost){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, cost);

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 30);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        Location start = player.getLocation();

        World world = player.getWorld();
        List<Player> allPlayersInWorld = world.getPlayers();

        List<Player> allValidPlayers = new ArrayList<>();

        for(Player thisPlayer : allPlayersInWorld){

            boolean deathStatus = profileManager.getAnyProfile(thisPlayer).getIfDead();

            if(deathStatus){
                continue;
            }

            if(pvpManager.pvpLogic(player, thisPlayer)){
                continue;
            }

            boolean hasBuffAlready = buffAndDebuffManager.getWildRoarBuff().getBuffTime(thisPlayer) > 0;

            if(hasBuffAlready){
                continue;
            }

            allValidPlayers.add(thisPlayer);

        }


        allValidPlayers.sort(Comparator.comparingDouble(p -> p.getLocation().distance(start)));
        List<Player> affected = allValidPlayers.subList(0, Math.min(5, allValidPlayers.size()));

        double level = profileManager.getAnyProfile(player).getStats().getLevel();

        double buffAmountPerLevel = 1.25;

        for(Player thisPlayer : affected){

            buffAndDebuffManager.getWildRoarBuff().applyBuff(thisPlayer, level * buffAmountPerLevel);

            ArmorStand armorStand = player.getWorld().spawn(start.clone().subtract(0,5,0), ArmorStand.class);
            armorStand.setInvisible(true);
            armorStand.setGravity(false);
            armorStand.setCollidable(false);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);

            EntityEquipment entityEquipment = armorStand.getEquipment();

            ItemStack buffIcon = new ItemStack(Material.ARROW);
            ItemMeta meta = buffIcon.getItemMeta();
            assert meta != null;

            meta.setCustomModelData(7);

            buffIcon.setItemMeta(meta);
            assert entityEquipment != null;
            entityEquipment.setHelmet(buffIcon);

            new BukkitRunnable(){
                int count = 0;
                @Override
                public void run(){

                    armorStand.teleport(thisPlayer.getLocation());

                    if(count >= 40){
                        cancelTask();
                    }

                    count ++;
                }

                private void cancelTask() {
                    this.cancel();
                    armorStand.remove();
                }

            }.runTaskTimer(main, 0, 1);
        }



    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;

    }

}
