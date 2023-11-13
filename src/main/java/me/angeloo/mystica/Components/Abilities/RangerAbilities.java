package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Ranger.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RangerAbilities {

    private final ProfileManager profileManager;

    private final RallyingCry rallyingCry;
    private final WildRoar wildRoar;
    private final StarVolley starVolley;
    private final Relentless relentless;
    private final RazorWind razorWind;
    private final WildSpirit wildSpirit;
    private final BlessedArrow blessedArrow;
    private final Roll roll;
    private final ShadowCrows shadowCrows;
    private final BitingRain bitingRain;
    private final RangerBasic rangerBasic;

    public RangerAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        starVolley = new StarVolley(main, manager);
        rallyingCry = new RallyingCry(main, manager);
        wildRoar = new WildRoar(main, manager);
        relentless = new Relentless(main, manager, this);
        razorWind = new RazorWind(main, manager, this);
        wildSpirit = new WildSpirit(main, manager, this);
        blessedArrow = new BlessedArrow(main, manager, this);
        roll = new Roll(main, manager);
        shadowCrows = new ShadowCrows(main, manager, this);
        bitingRain = new BitingRain(main, manager, this);
        rangerBasic = new RangerBasic(main, manager, this);
    }

    public void useRangerAbility(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                bitingRain.use(player);
                return;
            }
            case 2:{
                shadowCrows.use(player);
                return;
            }
            case 3:{
                relentless.use(player);
                return;
            }
            case 4:{
                razorWind.use(player);
                return;
            }
            case 5:{
                blessedArrow.use(player);
                return;
            }
            case 6:{
                rallyingCry.use(player);
                return;
            }
            case 7:{
                wildSpirit.sendSignal(player);
                return;
            }
            case 8:{
                roll.use(player);
                return;
            }
        }
    }

    public void useRangerUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "animal tamer":{
                wildRoar.use(player);
                return;
            }
            case "scout":{
                starVolley.use(player);
                return;
            }
        }
    }

    public void useRangerBasic(Player player){

        rangerBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return bitingRain.getCooldown(player);
            case 2:
                return shadowCrows.getCooldown(player);
            case 3:
                return relentless.getCooldown(player);
            case 4:
                return razorWind.getCooldown(player);
            case 5:
                return blessedArrow.getCooldown(player);
            case 6:
                return rallyingCry.getCooldown(player);
            case 7:
                return wildSpirit.getCooldown(player);
            case 8:
                return roll.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "animal tamer":
                return wildRoar.getCooldown(player);
            case "scout":
                return starVolley.getCooldown(player);
        }

        return 0;
    }

    public RallyingCry getRallyingCry() {
        return rallyingCry;
    }

    public StarVolley getStarVolley() {
        return starVolley;
    }
}
