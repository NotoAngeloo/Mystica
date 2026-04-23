package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class ElementalistAbilities implements AbilitySet {


    /*private final Heat heat;
    private final Ability fieryWing;
    private final Ability conjuringForce;
    private final Ability elementalBreath;
    private final Ability fieryMagma;
    private final Ability descendingInferno;
    private final Ability elemental_matrix;
    private final Ability iceBolt;
    private final Ability dragonBreathing;
    private final Ability windrushForm;
    private final ElementalistBasic elementalistBasic;
    private final Ability windWall;*/



    private final Map<Integer, Ability> abilities = new HashMap<>();

    public ElementalistAbilities(Mystica main, AbilityManager manager, AbilityLookup lookup){

        abilities.put(1, new IceBolt(main, manager));
        abilities.put(2, new FieryMagma(main, manager));
        abilities.put(3, new DescendingInferno(main, manager));
        abilities.put(4, new WindrushForm(main, manager));
        abilities.put(5, new WindWall(main, manager));
        abilities.put(6, new DragonBreathing(main, manager));
        abilities.put(7, new ElementalBreath(main, manager));
        abilities.put(8, new ElementalMatrix(main, manager));

        for(Ability ability : abilities.values()){
            if(ability instanceof BaseAbility base){
                base.setLookup(lookup);
            }
        }

        //elementalistBasic = new ElementalistBasic(main);
    }

    @Override
    public Ability get(int abilityNumber){
        return abilities.get(abilityNumber);
    }

    /*public Ability get(int abilityNumber){
        return switch (abilityNumber){
            case 1 -> iceBolt;
            case 2 -> fieryMagma;
            case 3 -> descendingInferno;
            case 4 -> windrushForm;
            case 5 -> windWall;
            case 6 -> dragonBreathing;
            case 7 -> elementalBreath;
            case 8 -> elemental_matrix;
            default -> null;
        };
    }*/

    /*public void useElementalistAbility(LivingEntity caster, int abilityNumber){

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

    }*/

    public void useElementalistBasic(LivingEntity caster){

        //elementalistBasic.use(caster);
    }


    /*public FieryWing getFieryWing(){
        return fieryWing;
    }
    public ElementalBreath getElementalBreath() {
        return elementalBreath;
    }
    public Ability getIceBolt(){return iceBolt;}
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
    public Heat getHeat(){return heat;}*/
}
