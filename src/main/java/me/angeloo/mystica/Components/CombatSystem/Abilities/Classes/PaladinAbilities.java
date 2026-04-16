package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Ability;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilitySet;
import me.angeloo.mystica.Mystica;

import java.util.HashMap;
import java.util.Map;

public class PaladinAbilities implements AbilitySet {

    /*private final Purity purity;

    private final GloryOfPaladins gloryOfPaladins;
    private final PaladinBasic paladinBasic;
    private final TorahSword torahSword;
    private final DivineGuidance divineGuidance;
    private final CovenantSword covenantSword;
    private final ReigningSword reigningSword;
    private final OrderShield orderShield;
    private final DuranceOfTruth duranceOfTruth;
    private final Judgement judgement;
    private final LightWell lightWell;
    private final SanctityShield sanctityShield;
    private final Decision decision;

    private final JusticeMark justiceMark;
    private final MercifulHealing mercifulHealing;
    private final DecreeHonor decreeHonor;
    private final HonorCounter honorCounter;
    private final DivineInfusion divineInfusion;
    private final SpiritualGift spiritualGift;
    private final SacredAegis sacredAegis;
    private final ModestCalling modestCalling;
    private final Representative representative;*/

    private final Map<Integer, Ability> abilities = new HashMap<>();

    public PaladinAbilities(Mystica main, AbilityManager manager){

        /*purity = new Purity(main);

        decision = new Decision();
        justiceMark = new JusticeMark(main, manager);
        representative = new Representative(main, manager);

        judgement = new Judgement(main, manager, this);
        covenantSword = new CovenantSword(main, manager, this);
        reigningSword = new ReigningSword(main, manager, this);
        gloryOfPaladins = new GloryOfPaladins(main, manager, this);
        paladinBasic = new PaladinBasic(main, this);
        divineGuidance = new DivineGuidance(main, manager, this);
        orderShield = new OrderShield(main, manager, this);
        duranceOfTruth = new DuranceOfTruth(main, manager, this);
        lightWell = new LightWell(main, manager);
        sanctityShield = new SanctityShield(main, manager);
        torahSword = new TorahSword(main, manager, this);

        mercifulHealing = new MercifulHealing(main, manager, this);
        decreeHonor = new DecreeHonor(main, manager, this);
        honorCounter = new HonorCounter(main, manager, this);
        divineInfusion = new DivineInfusion(main, manager, this);
        spiritualGift = new SpiritualGift(main, manager, this);
        sacredAegis = new SacredAegis(main, manager);
        modestCalling = new ModestCalling(main, manager, this);*/

        abilities.put(1, new TorahSword(main, manager));
        abilities.put(2, new DivineGuidance(main, manager));
        abilities.put(3, new ReigningSword(main, manager));
        abilities.put(4, new CovenantSword(main, manager));
        abilities.put(5, new OrderShield(main, manager));
        abilities.put(6, new GloryOfPaladins(main, manager));
        abilities.put(7, new DuranceOfTruth(main, manager));
        abilities.put(8, new Judgement(main, manager));

    }

    @Override
    public Ability get(int abilityNumber){
        return abilities.get(abilityNumber);
    }


    /*public void usePaladinAbility(LivingEntity caster, int abilityNumber){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Divine)){
            switch (abilityNumber) {
                case 1 -> {
                    decreeHonor.use(caster);
                    return;
                }
                case 2 -> {
                    mercifulHealing.use(caster);
                    return;
                }
                case 3 -> {
                    honorCounter.use(caster);
                    return;
                }
                case 4 -> {
                    divineInfusion.use(caster);
                    return;
                }
                case 5 -> {
                    spiritualGift.use(caster);
                    return;
                }
                case 6 -> {
                    sacredAegis.use(caster);
                    return;
                }
                case 7 -> {
                    modestCalling.use(caster);
                    return;
                }
                case 8 -> {
                    justiceMark.use(caster);
                    return;
                }
            }
        }

        switch (abilityNumber) {
            case 1 -> {
                torahSword.use(caster);
                return;
            }
            case 2 -> {
                divineGuidance.use(caster);
                return;
            }
            case 3 -> {
                reigningSword.use(caster);
                return;
            }
            case 4 -> {
                covenantSword.use(caster);
                return;
            }
            case 5 -> {
                orderShield.use(caster);
                return;
            }
            case 6 -> {
                gloryOfPaladins.use(caster);
                return;
            }
            case 7 -> {
                duranceOfTruth.use(caster);
                return;
            }
            case 8 -> {
                judgement.use(caster);
                return;
            }
        }
    }

    public void usePaladinUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass) {
            case Templar -> {
                sanctityShield.use(caster);
                return;
            }
            case Divine -> {
                representative.use(caster);
                return;
            }
            case Dawn -> {
                lightWell.use(caster);
                return;
            }
        }
    }

    public void usePaladinBasic(LivingEntity caster){
        paladinBasic.useBasic(caster);
    }


    public Purity getPurity(){return purity;}
    public Representative getRepresentative(){return representative;}
    public MercifulHealing getMercifulHealing(){return mercifulHealing;}
    public JusticeMark getJusticeMark(){return justiceMark;}
    public Decision getDecision(){return decision;}
    public GloryOfPaladins getGloryOfPaladins(){
        return gloryOfPaladins;
    }
    public Judgement getJudgement(){return judgement;}
    public DecreeHonor getDecreeHonor(){return decreeHonor;}
    public HonorCounter getHonorCounter(){return honorCounter;}
    public DivineInfusion getDivineInfusion(){return divineInfusion;}
    public SpiritualGift getSpiritualGift(){return spiritualGift;}
    public SacredAegis getSacredAegis(){return sacredAegis;}
    public ModestCalling getModestCalling(){return modestCalling;}
    public TorahSword getTorahSword(){return torahSword;}
    public DivineGuidance getDivineGuidance(){return divineGuidance;}
    public ReigningSword getReigningSword(){return reigningSword;}
    public CovenantSword getCovenantSword(){return covenantSword;}
    public OrderShield getOrderShield(){return orderShield;}
    public DuranceOfTruth getDuranceOfTruth(){return duranceOfTruth;}
    public SanctityShield getSanctityShield(){return sanctityShield;}
    public LightWell getLightWell(){return lightWell;}
    public PaladinBasic getPaladinBasic(){return paladinBasic;}*/
}
