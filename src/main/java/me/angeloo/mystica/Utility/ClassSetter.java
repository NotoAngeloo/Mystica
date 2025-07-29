package me.angeloo.mystica.Utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;

import me.angeloo.mystica.CustomEvents.MaxHealthChangeOutOfCombatEvent;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
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


        MysticaEquipment weapon = profileManager.getAnyProfile(player).getPlayerEquipment().getWeapon();
        MysticaEquipment helmet = profileManager.getAnyProfile(player).getPlayerEquipment().getHelmet();
        MysticaEquipment chest = profileManager.getAnyProfile(player).getPlayerEquipment().getChestPlate();
        MysticaEquipment legs = profileManager.getAnyProfile(player).getPlayerEquipment().getLeggings();
        MysticaEquipment boots = profileManager.getAnyProfile(player).getPlayerEquipment().getBoots();

        if(weapon != null){
            weapon.setPlayerClass(playerClass);
        }

        if(helmet != null){
            helmet.setPlayerClass(playerClass);
        }

        if(chest != null){
            chest.setPlayerClass(playerClass);
        }

        if(legs != null){
            legs.setPlayerClass(playerClass);
        }

        if(boots != null){
            boots.setPlayerClass(playerClass);
        }



        displayWeapons.displayArmor(player);
        gearReader.setGearStats(player);
        Bukkit.getServer().getPluginManager().callEvent(new MaxHealthChangeOutOfCombatEvent(player));
        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Resource, true));

    }


}
