package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist.*;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
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
        fieryWing = new FieryWing(main, this, manager);
        conjuringForce = new ConjuringForce(main, manager);
        elementalBreath = new ElementalBreath(main, manager);
        fieryMagma = new FieryMagma(main, manager, this);
        descendingInferno = new DescendingInferno(main, manager, this);
        elemental_matrix = new ElementalMatrix(main, manager);
        iceBolt = new IceBolt(main, manager, this);
        dragonBreathing = new DragonBreathing(main, manager, this);
        windrushForm = new WindrushForm(main, manager, this);
        windWall = new WindWall(main, manager, this);
        elementalistBasic = new ElementalistBasic(main);
    }

    public void useElementalistAbility(LivingEntity caster, int abilityNumber){

        switch (abilityNumber) {
            case 1 -> {
                iceBolt.use(caster);
                return;
            }
            case 2 -> {
                fieryMagma.use(caster);
                return;
            }
            case 3 -> {
                descendingInferno.use(caster);
                return;
            }
            case 4 -> {
                windrushForm.use(caster);
                return;
            }
            case 5 -> {
                windWall.use(caster);
                return;
            }
            case 6 -> {
                dragonBreathing.use(caster);
                return;
            }
            case 7 -> {
                elementalBreath.use(caster);
                return;
            }
            case 8 -> {
                elemental_matrix.use(caster);
                return;
            }
        }

    }

    public void useElementalistUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass) {
            case Pyromancer -> {
                fieryWing.use(caster);
                return;
            }
            case Conjurer -> {
                conjuringForce.use(caster);
                return;
            }
        }

    }

    public void useElementalistBasic(LivingEntity caster){

        elementalistBasic.use(caster);
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
