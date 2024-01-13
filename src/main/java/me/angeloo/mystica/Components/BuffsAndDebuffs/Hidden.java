package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.AggroManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Hidden {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AggroManager aggroManager;

    public Hidden(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        aggroManager = main.getAggroManager();
    }

    public void hidePlayer(Player player, boolean blacklist){

        player.setInvisible(true);
        player.getInventory().setItemInMainHand(null);
        player.getInventory().setItemInOffHand(null);
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

        if(blacklist){
            aggroManager.removeHighPriorityTarget(player.getUniqueId());
            aggroManager.addToBlackList(player);
            aggroManager.removeFromAllAttackerLists(player);
        }


    }

    public void unhidePlayer(Player player){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();
        boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));

        if(!deathStatus){
            player.setInvisible(false);

            if(!combatStatus){
                return;
            }

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(playerEquipment.getWeapon() != null){
                player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
            }

            if (playerEquipment.getOffhand() != null){
                player.getInventory().setItemInOffHand(playerEquipment.getOffhand());
            }

            DisplayWeapons displayWeapons  = new DisplayWeapons(main);
            displayWeapons.displayArmor(player);
            aggroManager.removeFromBlackList(player);
        }

    }

    public void showWeapons(Player player){
        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(playerEquipment.getWeapon() != null){
            player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
        }

        if (playerEquipment.getOffhand() != null){
            player.getInventory().setItemInOffHand(playerEquipment.getOffhand());
        }
    }

}


