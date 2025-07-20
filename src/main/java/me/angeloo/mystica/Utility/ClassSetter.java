package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;



public class ClassSetter {

    private final ProfileManager profileManager;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;

    public ClassSetter(Mystica main){
        profileManager = main.getProfileManager();
        displayWeapons = main.getDisplayWeapons();
        gearReader = new GearReader(main);
    }

    public void setClass(Player player, PlayerClass playerClass){


        Profile playerProfile = profileManager.getAnyProfile(player);

        if(playerProfile.getPlayerClass().equals(playerClass)){
            player.sendMessage("you are already this class");
            return;
        }


        playerProfile.setPlayerClass(playerClass);

        playerProfile.setPlayerSubclass(SubClass.NONE);
        profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(), playerClass, SubClass.NONE);
        player.sendMessage("You are now a(n) " + playerClass.name());


        displayWeapons.displayArmor(player);
        gearReader.setGearStats(player);

        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "resource", true));

    }


}
