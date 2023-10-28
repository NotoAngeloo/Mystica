package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassEquipment.ElementalistEquipment;
import me.angeloo.mystica.Components.ClassEquipment.MysticEquipment;
import me.angeloo.mystica.Components.ClassEquipment.RangerEquipment;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class ClassSetter {

    private final ProfileManager profileManager;
    private final ElementalistEquipment elementalistEquipment;
    private final RangerEquipment rangerEquipment;
    private final MysticEquipment mysticEquipment;
    private final DisplayWeapons displayWeapons;

    public ClassSetter(Mystica main){
        profileManager = main.getProfileManager();
        elementalistEquipment = new ElementalistEquipment();
        rangerEquipment = new RangerEquipment();
        mysticEquipment = new MysticEquipment();
        displayWeapons = new DisplayWeapons(main);
    }

    public void setClass(Player player, String clazz, Boolean trial){

        if(trial){
            profileManager.setClassTrial(player, clazz);
            player.sendMessage("You are now trying out " + clazz);
            displayWeapons.displayWeapons(player);
            return;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);

        switch(clazz.toLowerCase()){
            case "elementalist":{
                playerProfile.getPlayerEquipment().setWeapon(elementalistEquipment.getBaseWeapon());
                playerProfile.getPlayerEquipment().setOffhand(elementalistEquipment.getBaseOffhand());
                playerProfile.getPlayerEquipment().setHelmet(elementalistEquipment.getBaseHelmet());
                playerProfile.getPlayerEquipment().setChestPlate(elementalistEquipment.getBaseChestPlate());
                playerProfile.getPlayerEquipment().setLeggings(elementalistEquipment.getBaseLeggings());
                playerProfile.getPlayerEquipment().setBoots(elementalistEquipment.getBaseBoots());
                break;
            }
            case "ranger":{
                playerProfile.getPlayerEquipment().setWeapon(rangerEquipment.getBaseWeapon());
                playerProfile.getPlayerEquipment().setOffhand(rangerEquipment.getBaseOffhand());
                playerProfile.getPlayerEquipment().setHelmet(rangerEquipment.getBaseHelmet());
                playerProfile.getPlayerEquipment().setChestPlate(rangerEquipment.getBaseChestPlate());
                playerProfile.getPlayerEquipment().setLeggings(rangerEquipment.getBaseLeggings());
                playerProfile.getPlayerEquipment().setBoots(rangerEquipment.getBaseBoots());
                break;
            }
            case "mystic":{
                playerProfile.getPlayerEquipment().setWeapon(mysticEquipment.getBaseWeapon());
                playerProfile.getPlayerEquipment().setOffhand(mysticEquipment.getBaseOffhand());
                playerProfile.getPlayerEquipment().setHelmet(mysticEquipment.getBaseHelmet());
                playerProfile.getPlayerEquipment().setChestPlate(mysticEquipment.getBaseChestPlate());
                playerProfile.getPlayerEquipment().setLeggings(mysticEquipment.getBaseLeggings());
                playerProfile.getPlayerEquipment().setBoots(mysticEquipment.getBaseBoots());
            }
        }

        playerProfile.setPlayerClass(clazz);

        player.sendMessage("You are now a(n) " + clazz);
        displayWeapons.displayWeapons(player);

    }

}
