package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Assassin.AssassinBasic;
import me.angeloo.mystica.Components.Abilities.Assassin.Combo;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class AssassinAbilities {

    private final ProfileManager profileManager;

    private final Combo combo;

    private final AssassinBasic assassinBasic;

    public AssassinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();

        combo = new Combo(main);
        assassinBasic = new AssassinBasic(main, manager, this);
    }

    public void useAssassinAbility(Player player, int abilityNumber){

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

    public void useAssassinUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "assassinator":{
                return;
            }
            case "alchemist":{
                return;
            }
        }
    }

    public void useAssassinBasic(Player player){
        assassinBasic.useBasic(player);
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
            case "assassinator":

            case "alchemist":


        }

        return 0;
    }

    public Combo getCombo(){return combo;}

}
