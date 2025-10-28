package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Assassin.*;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
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
        stealth = new Stealth(main, manager, this);
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

    public void useAssassinAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:{
                assault.use(caster);
                return;
            }
            case 2:{
                laceration.use(caster);
                return;
            }
            case 3:{
                weaknessStrike.use(caster);
                return;
            }
            case 4:{
                pierce.use(caster);
                return;
            }
            case 5:{
                dash.use(caster);
                return;
            }
            case 6:{
                bladeTempest.use(caster);
                return;
            }
            case 7:{
                flyingBlade.use(caster);
                return;
            }
            case 8:{
                stealth.toggle(caster);
                return;
            }
        }
    }

    public void useAssassinUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass){
            case Duelist:{
                duelistsFrenzy.use(caster);
                return;
            }
            case Alchemist:{
                wickedConcoction.use(caster);
                return;
            }
        }
    }

    public void useAssassinBasic(LivingEntity caster){
        assassinBasic.useBasic(caster);
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
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Duelist:
                return duelistsFrenzy.getSkillCooldown();
            case Alchemist:
                return wickedConcoction.getSkillCooldown();

        }

        return 0;
    }

    public int getPlayerUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Duelist:
                return duelistsFrenzy.getPlayerCooldown(player);
            case Alchemist:
                return wickedConcoction.getPlayerCooldown(player);

        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        assault.resetCooldown(caster);
        bladeTempest.resetCooldown(caster);
        dash.resetCooldown(caster);
        duelistsFrenzy.resetCooldown(caster);
        flyingBlade.resetCooldown(caster);
        laceration.resetCooldown(caster);
        pierce.resetCooldown(caster);
        stealth.resetCooldown(caster);
        weaknessStrike.resetCooldown(caster);
        wickedConcoction.resetCooldown(caster);
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
