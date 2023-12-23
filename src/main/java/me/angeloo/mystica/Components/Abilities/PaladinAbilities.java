package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Paladin.CovenantSword;
import me.angeloo.mystica.Components.Abilities.Paladin.DivineGuidance;
import me.angeloo.mystica.Components.Abilities.Paladin.PaladinBasic;
import me.angeloo.mystica.Components.Abilities.Paladin.TorahSword;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class PaladinAbilities {

    private final ProfileManager profileManager;

    private final PaladinBasic paladinBasic;
    private final TorahSword torahSword;
    private final DivineGuidance divineGuidance;
    private final CovenantSword covenantSword;

    public PaladinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        paladinBasic = new PaladinBasic(main, manager);
        torahSword = new TorahSword(main, manager);
        divineGuidance = new DivineGuidance(main, manager);
        covenantSword = new CovenantSword(main, manager);
    }

    public void usePaladinAbility(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
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

        switch (abilityNumber){
            case 1:{
                torahSword.use(player);
                return;
            }
            case 2:{
                divineGuidance.use(player);
                return;
            }
            case 3:{
                return;
            }
            case 4:{
                covenantSword.use(player);
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
        paladinBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
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
        }

        switch (abilityNumber){
            case 1:
                return torahSword.getCooldown(player);
            case 2:
                return divineGuidance.getCooldown(player);
            case 3:

            case 4:
                return covenantSword.getCooldown(player);
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
