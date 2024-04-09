package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Assassin.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class AssassinAbilities {

    private final ProfileManager profileManager;

    private final Combo combo;
    private final Stealth stealth;
    private final AssassinBasic assassinBasic;
    private final Assault assault;
    private final Laceration laceration;
    private final WeaknessStrike weaknessStrike;
    private final Pierce pierce;
    private final Dash dash;
    private final BladeTempest bladeTempest;
    private final FlyingBlade flyingBlade;
    private final DuelistsFrenzy duelistsFrenzy;
    private final WickedConcoction wickedConcoction;

    public AssassinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        combo = new Combo(main, manager);
        stealth = new Stealth(main, manager);
        duelistsFrenzy = new DuelistsFrenzy(main, manager, this);
        assassinBasic = new AssassinBasic(main, manager, this);
        assault = new Assault(main, manager, this);
        laceration = new Laceration(main, manager, this);
        weaknessStrike = new WeaknessStrike(main, manager, this);
        pierce = new Pierce(main, manager, this);
        dash = new Dash(main, manager);
        bladeTempest = new BladeTempest(main, manager, this);
        flyingBlade = new FlyingBlade(main, manager, this);
        wickedConcoction = new WickedConcoction(main, manager, this);
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
                stealth.toggle(player);
                return;
            }
        }
    }

    public void useAssassinUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "duelist":{
                duelistsFrenzy.use(player);
                return;
            }
            case "alchemist":{
                wickedConcoction.use(player);
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
                return stealth.getCooldown(player);

        }

        return 0;
    }



    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "duelist":
                return duelistsFrenzy.getCooldown(player);
            case "alchemist":
                return wickedConcoction.getCooldown(player);

        }

        return 0;
    }

    public Stealth getStealth(){return stealth;}
    public Combo getCombo(){return combo;}
    public DuelistsFrenzy getDuelistsFrenzy(){return duelistsFrenzy;}
    public Assault getAssault(){return assault;}
    public Laceration getLaceration(){return laceration;}
    public WeaknessStrike getWeaknessStrike(){return weaknessStrike;}
    public Pierce getPierce(){return pierce;}
    public Dash getDash(){return dash;}
    public BladeTempest getBladeTempest(){return bladeTempest;}
    public FlyingBlade getFlyingBlade(){return flyingBlade;}
    public WickedConcoction getWickedConcoction(){return wickedConcoction;}
    public AssassinBasic getAssassinBasic(){return assassinBasic;}

}
