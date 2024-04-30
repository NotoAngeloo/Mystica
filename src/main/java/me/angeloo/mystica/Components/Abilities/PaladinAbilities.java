package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Paladin.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class PaladinAbilities {

    private final ProfileManager profileManager;

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
    private final Representative representative;

    public PaladinAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        decision = new Decision();
        justiceMark = new JusticeMark(main, manager);
        representative = new Representative(main, manager);

        judgement = new Judgement(main, manager, this);
        covenantSword = new CovenantSword(main, manager, this);
        reigningSword = new ReigningSword(main, manager, this);
        gloryOfPaladins = new GloryOfPaladins(main, manager);
        paladinBasic = new PaladinBasic(main, manager, this);
        divineGuidance = new DivineGuidance(main, manager);
        orderShield = new OrderShield(main, manager);
        duranceOfTruth = new DuranceOfTruth(main, manager);
        lightWell = new LightWell(main, manager);
        sanctityShield = new SanctityShield(main, manager);
        torahSword = new TorahSword(main, manager, this);

        mercifulHealing = new MercifulHealing(main, manager, this);
        decreeHonor = new DecreeHonor(main, manager, this);
        honorCounter = new HonorCounter(main, manager);
        divineInfusion = new DivineInfusion(main, manager);
        spiritualGift = new SpiritualGift(main, manager);
        sacredAegis = new SacredAegis(main, manager);
        modestCalling = new ModestCalling(main, manager);


    }

    public void usePaladinAbility(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
            switch (abilityNumber){
                case 1:{
                    decreeHonor.use(player);
                    return;
                }
                case 2:{
                    mercifulHealing.use(player);
                    return;
                }
                case 3:{
                    honorCounter.use(player);
                    return;
                }
                case 4:{
                    divineInfusion.use(player);
                    return;
                }
                case 5:{
                    spiritualGift.use(player);
                    return;
                }
                case 6:{
                    sacredAegis.use(player);
                    return;
                }
                case 7:{
                    modestCalling.use(player);
                    return;
                }
                case 8:{
                    justiceMark.use(player);
                    return;
                }
            }
        }

        switch (abilityNumber){
            case 1:{
                torahSword.use(player);
                return;
            }
            case 2:{
                divineGuidance.use(player);
                return;
            }
            case 3:{
                reigningSword.use(player);
                return;
            }
            case 4:{
                covenantSword.use(player);
                return;
            }
            case 5:{
                orderShield.use(player);
                return;
            }
            case 6:{
                gloryOfPaladins.use(player);
                return;
            }
            case 7:{
                duranceOfTruth.use(player);
                return;
            }
            case 8:{
                judgement.use(player);
                return;
            }
        }
    }

    public void usePaladinUltimate(Player player){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "templar":{
                sanctityShield.use(player);
                return;
            }
            case "divine":{
                representative.use(player);
                return;
            }
            case "dawn":{
                lightWell.use(player);
                return;
            }
        }
    }

    public void usePaladinBasic(Player player){
        paladinBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equalsIgnoreCase("divine")){
            switch (abilityNumber){
                case 1:
                    return decreeHonor.getCooldown(player);
                case 2:
                    return mercifulHealing.getCooldown(player);
                case 3:
                    return honorCounter.getCooldown(player);
                case 4:
                    return divineInfusion.getCooldown(player);
                case 5:
                    return spiritualGift.getCooldown(player);
                case 6:
                    return sacredAegis.getCooldown(player);
                case 7:
                    return modestCalling.getCooldown(player);
                case 8:
                    return justiceMark.getCooldown(player);
            }
        }

        switch (abilityNumber){
            case 1:
                return torahSword.getCooldown(player);
            case 2:
                return divineGuidance.getCooldown(player);
            case 3:
                return reigningSword.getCooldown(player);
            case 4:
                return covenantSword.getCooldown(player);
            case 5:
                return orderShield.getCooldown(player);
            case 6:
                return gloryOfPaladins.getCooldown(player);
            case 7:
                return duranceOfTruth.getCooldown(player);
            case 8:
                return judgement.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass.toLowerCase()){
            case "templar":
                return sanctityShield.getCooldown(player);
            case "divine":
                return representative.getCooldown(player);
            case "dawn":
                return lightWell.getCooldown(player);
        }

        return 0;
    }

    public void resetCooldowns(Player player){
        covenantSword.resetCooldown(player);
        decreeHonor.resetCooldown(player);
        divineGuidance.resetCooldown(player);
        duranceOfTruth.resetCooldown(player);
        gloryOfPaladins.resetCooldown(player);
        honorCounter.resetCooldown(player);
        judgement.resetCooldown(player);
        justiceMark.resetCooldown(player);
        lightWell.resetCooldown(player);
        mercifulHealing.resetCooldown(player);
        modestCalling.resetCooldown(player);
        orderShield.resetCooldown(player);
        reigningSword.resetCooldown(player);
        representative.resetCooldown(player);
        sacredAegis.resetCooldown(player);
        sanctityShield.resetCooldown(player);
        spiritualGift.resetCooldown(player);
        torahSword.resetCooldown(player);
    }

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
    public PaladinBasic getPaladinBasic(){return paladinBasic;}
}
