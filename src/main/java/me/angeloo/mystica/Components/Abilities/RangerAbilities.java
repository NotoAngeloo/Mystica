package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Ranger.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RangerAbilities {

    private final ProfileManager profileManager;

    private final Focus focus;
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
        focus = new Focus(main);
        starVolley = new StarVolley(main, manager, this);
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

    public void useRangerAbility(LivingEntity caster, int abilityNumber){

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


    public int getAbilityCooldown(LivingEntity caster, int abilityNumber){

        return switch (abilityNumber) {
            case 1 -> bitingRain.getCooldown(caster);
            case 2 -> shadowCrows.getCooldown(caster);
            case 3 -> relentless.getCooldown(caster);
            case 4 -> razorWind.getCooldown(caster);
            case 5 -> blessedArrow.getCooldown(caster);
            case 6 -> rallyingCry.getCooldown(caster);
            case 7 -> wildSpirit.getCooldown(caster);
            case 8 -> roll.getCooldown(caster);
            default -> 0;
        };

    }

    public int getPlayerUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        return switch (subclass) {
            case Tamer -> wildRoar.getPlayerCooldown(player);
            case Scout -> starVolley.getPlayerCooldown(player);
            default -> 0;
        };

    }

    public int getUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        return switch (subclass) {
            case Tamer -> wildRoar.getSkillCooldown();
            case Scout -> starVolley.getSkillCooldown();
            default -> 0;
        };

    }

    public void resetCooldowns(LivingEntity caster){
        bitingRain.resetCooldown(caster);
        blessedArrow.resetCooldown(caster);
        rallyingCry.resetCooldown(caster);
        razorWind.resetCooldown(caster);
        relentless.resetCooldown(caster);
        roll.resetCooldown(caster);
        shadowCrows.resetCooldown(caster);
        starVolley.resetCooldown(caster);
        wildRoar.resetCooldown(caster);
        wildSpirit.resetCooldown(caster);
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
    public Focus getFocus(){return focus;}
}
