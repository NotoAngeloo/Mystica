package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class PaladinAbilities {

    private final ProfileManager profileManager;

    public PaladinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();

    }

    public void usePaladinAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                return;
            }
            case 2:{
                return;
            }
            case 3:{
                return;
            }
            case 4:{
                return;
            }
            case 5:{
                return;
            }
            case 6:{
                return;
            }
            case 7:{
                return;
            }
            case 8:{
                return;
            }
        }
    }

    public void usePaladinUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "templar":{
                return;
            }
            case "divine":{
                return;
            }
            case "dawn":{
                return;
            }
        }
    }

    public void usePaladinBasic(Player player){

    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:

            case 2:

            case 3:

            case 4:

            case 5:

            case 6:

            case 7:

            case 8:

        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "templar":

            case "divine":

            case "dawn":

        }

        return 0;
    }

}