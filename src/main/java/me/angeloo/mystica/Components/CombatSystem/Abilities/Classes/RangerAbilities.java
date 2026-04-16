package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Ability;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class RangerAbilities implements AbilitySet {


    /*private final Focus focus;
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
    private final RangerBasic rangerBasic;*/

    private final Map<Integer, Ability> abilities = new HashMap<>();

    public RangerAbilities(Mystica main, AbilityManager manager){

        /*ocus = new Focus(main);
        starVolley = new StarVolley(main, this, manager);
        rallyingCry = new RallyingCry(main, manager);
        wildRoar = new WildRoar(main, manager);
        relentless = new Relentless(main, manager, this);
        razorWind = new RazorWind(main, manager, this);
        wildSpirit = new WildSpirit(main, manager, this);
        blessedArrow = new BlessedArrow(main, manager, this);
        roll = new Roll(main, manager);
        shadowCrows = new ShadowCrows(main, manager, this);
        bitingRain = new BitingRain(main, manager, this);
        rangerBasic = new RangerBasic(main, this);*/

        abilities.put(1, new BitingRain(main, manager));
        abilities.put(2, new ShadowCrows(main, manager));
        abilities.put(3, new Relentless(main, manager));
        abilities.put(4, new RazorWind(main, manager));
        abilities.put(5, new BlessedArrow(main, manager));
        abilities.put(6, new RallyingCry(main, manager));
        abilities.put(7, new WildSpirit(main, manager));
        abilities.put(8, new Roll(main, manager));
    }

    @Override
    public Ability get(int abilityNumber){
        return abilities.get(abilityNumber);
    }

    /*public void useRangerAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber) {
            case 1 -> {
                bitingRain.use(caster);
                return;
            }
            case 2 -> {
                shadowCrows.use(caster);
                return;
            }
            case 3 -> {
                relentless.use(caster);
                return;
            }
            case 4 -> {
                razorWind.use(caster);
                return;
            }
            case 5 -> {
                blessedArrow.use(caster);
                return;
            }
            case 6 -> {
                rallyingCry.use(caster);
                return;
            }
            case 7 -> {
                wildSpirit.sendSignal(caster);
                return;
            }
            case 8 -> {
                roll.use(caster);
                return;
            }
        }
    }

    public void useRangerUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass) {
            case Tamer -> {
                wildRoar.use(caster);
                return;
            }
            case Scout -> {
                starVolley.use(caster);
                return;
            }
        }
    }

    public void useRangerBasic(LivingEntity caster){

        rangerBasic.useBasic(caster);
    }



    public RallyingCry getRallyingCry() {
        return rallyingCry;
    }
    public StarVolley getStarVolley() {
        return starVolley;
    }
    public BitingRain getBitingRain(){return bitingRain;}
    public ShadowCrows getShadowCrows(){return shadowCrows;}
    public Relentless getRelentless(){return relentless;}
    public RazorWind getRazorWind(){return razorWind;}
    public BlessedArrow getBlessedArrow(){return blessedArrow;}
    public WildSpirit getWildSpirit(){return wildSpirit;}
    public Roll getRoll(){return roll;}
    public WildRoar getWildRoar(){return wildRoar;}
    public RangerBasic getRangerBasic(){return rangerBasic;}
    public Focus getFocus(){return focus;}*/
}
