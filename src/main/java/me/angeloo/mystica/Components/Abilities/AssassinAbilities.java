package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Assassin.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class AssassinAbilities {

    private final ProfileManager profileManager;

    private final Combo combo;

    private final AssassinBasic assassinBasic;
    private final Assault assault;
    private final Laceration laceration;
    private final WeaknessStrike weaknessStrike;
    private final Pierce pierce;
    private final Dash dash;
    private final BladeTempest bladeTempest;
    private final FlyingBlade flyingBlade;

    public AssassinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();

        combo = new Combo(main);
        assassinBasic = new AssassinBasic(main, manager, this);
        assault = new Assault(main, manager, this);
        laceration = new Laceration(main, manager, this);
        weaknessStrike = new WeaknessStrike(main, manager, this);
        pierce = new Pierce(main, manager, this);
        dash = new Dash(main, manager);
        bladeTempest = new BladeTempest(main, manager, this);
        flyingBlade = new FlyingBlade(main, manager);
    }

    public void useAssassinAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                assault.use(player);
                return;
            }
            case 2:{
                laceration.use(player);
                return;
            }
            case 3:{
                weaknessStrike.use(player);
                return;
            }
            case 4:{
                pierce.use(player);
                return;
            }
            case 5:{
                dash.use(player);
                return;
            }
            case 6:{
                bladeTempest.use(player);
                return;
            }
            case 7:{
                flyingBlade.use(player);
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
            case "duelist":{
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
                return assault.getCooldown(player);
            case 2:
                return laceration.getCooldown(player);
            case 3:
                return weaknessStrike.getCooldown(player);
            case 4:
                return pierce.getCooldown(player);
            case 5:
                return dash.getCooldown(player);
            case 6:
                return bladeTempest.getCooldown(player);
            case 7:
                return flyingBlade.getCooldown(player);
            case 8:

        }

        return 0;
    }

    public int getWeaknessStrikeModelData(Player player){
        return weaknessStrike.returnWhichItem(player);
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
