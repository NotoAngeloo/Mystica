package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Elementalist.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ElementalistAbilities {

    private final ProfileManager profileManager;
    private final Heat heat;
    private final FieryWing fieryWing;
    private final ConjuringForce conjuringForce;
    private final ElementalBreath elementalBreath;
    private final FieryMagma fieryMagma;
    private final DescendingInferno descendingInferno;
    private final ElementalMatrix elemental_matrix;
    private final IceBolt iceBolt;
    private final DragonBreathing dragonBreathing;
    private final WindrushForm windrushForm;
    private final WindWall windWall;
    private final ElementalistBasic elementalistBasic;

    public ElementalistAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        heat = new Heat(main);
        fieryWing = new FieryWing(main, manager, this);
        conjuringForce = new ConjuringForce(main, manager);
        elementalBreath = new ElementalBreath(main, manager);
        fieryMagma = new FieryMagma(main, manager, this);
        descendingInferno = new DescendingInferno(main, manager, this);
        elemental_matrix = new ElementalMatrix(main, manager);
        iceBolt = new IceBolt(main, manager, this);
        dragonBreathing = new DragonBreathing(main, manager, this);
        windrushForm = new WindrushForm(main, manager, this);
        windWall = new WindWall(main, manager, this);
        elementalistBasic = new ElementalistBasic(main, manager);
    }

    public void useElementalistAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber){
            case 1:
                iceBolt.use(caster);
                return;
            case 2:{
                fieryMagma.use(caster);
                return;
            }
            case 3:{
                descendingInferno.use(caster);
                return;
            }
            case 4:{
                windrushForm.use(caster);
                return;
            }
            case 5:{
                windWall.use(caster);
                return;
            }
            case 6:{
                dragonBreathing.use(caster);
                return;
            }
            case 7:
                elementalBreath.use(caster);
                return;
            case 8:
                elemental_matrix.use(caster);
                return;
        }

    }

    public void useElementalistUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass){
            case Pyromancer:{
                fieryWing.use(caster);
                return;
            }
            case Conjurer:{
                conjuringForce.use(caster);
                return;
            }
        }

    }

    public void useElementalistBasic(LivingEntity caster){



        elementalistBasic.use(caster);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return iceBolt.getCooldown(player);
            case 2:
                return fieryMagma.getCooldown(player);
            case 3:
                return descendingInferno.getCooldown(player);
            case 4:
                return windrushForm.getCooldown(player);
            case 5:
                return windWall.getCooldown(player);
            case 6:
                return dragonBreathing.getCooldown(player);
            case 7:
                return elementalBreath.getCooldown(player);
            case 8:
                return elemental_matrix.getCooldown(player);
        }

        return 0;
    }


    public int getUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Pyromancer:{
                return fieryWing.getSkillCooldown();
            }
            case Conjurer:{
                return conjuringForce.getSkillCooldown();
            }
        }

        return 0;
    }

    public int getPlayerUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Pyromancer:{
                return fieryWing.getPlayerCooldown(player);
            }
            case Conjurer:{
                return conjuringForce.getPlayerCooldown(player);
            }
        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        conjuringForce.resetCooldown(caster);
        descendingInferno.resetCooldown(caster);
        dragonBreathing.resetCooldown(caster);
        elementalBreath.resetCooldown(caster);
        elemental_matrix.resetCooldown(caster);
        fieryMagma.resetCooldown(caster);
        fieryWing.resetCooldown(caster);
        iceBolt.resetCooldown(caster);
        windrushForm.resetCooldown(caster);
        windWall.resetCooldown(caster);
    }

    public FieryWing getFieryWing(){
        return fieryWing;
    }
    public ElementalBreath getElementalBreath() {
        return elementalBreath;
    }
    public IceBolt getIceBolt(){return iceBolt;}
    public DescendingInferno getDescendingInferno(){return descendingInferno;}
    public FieryMagma getFieryMagma(){return fieryMagma;}
    public WindrushForm getWindrushForm() {
        return windrushForm;
    }
    public WindWall getWindWall() {
        return windWall;
    }
    public DragonBreathing getDragonBreathing(){return dragonBreathing;}
    public ElementalMatrix getElemental_matrix(){return elemental_matrix;}
    public ConjuringForce getConjuringForce(){return conjuringForce;}
    public ElementalistBasic getElementalistBasic(){return elementalistBasic;}
    public Heat getHeat(){return heat;}
}
