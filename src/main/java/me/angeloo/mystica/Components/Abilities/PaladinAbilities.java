package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Paladin.*;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PaladinAbilities {

    private final ProfileManager profileManager;

    private final Purity purity;

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

        purity = new Purity();

        decision = new Decision();
        justiceMark = new JusticeMark(main, manager, this);
        representative = new Representative(main, manager);

        judgement = new Judgement(main, manager, this);
        covenantSword = new CovenantSword(main, manager, this);
        reigningSword = new ReigningSword(main, manager, this);
        gloryOfPaladins = new GloryOfPaladins(main, manager, this);
        paladinBasic = new PaladinBasic(main, manager, this);
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
        sacredAegis = new SacredAegis(main, manager, this);
        modestCalling = new ModestCalling(main, manager, this);


    }

    public void usePaladinAbility(LivingEntity caster, int abilityNumber){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        if(subclass.equals(SubClass.Divine)){
            switch (abilityNumber){
                case 1:{
                    decreeHonor.use(caster);
                    return;
                }
                case 2:{
                    mercifulHealing.use(caster);
                    return;
                }
                case 3:{
                    honorCounter.use(caster);
                    return;
                }
                case 4:{
                    divineInfusion.use(caster);
                    return;
                }
                case 5:{
                    spiritualGift.use(caster);
                    return;
                }
                case 6:{
                    sacredAegis.use(caster);
                    return;
                }
                case 7:{
                    modestCalling.use(caster);
                    return;
                }
                case 8:{
                    justiceMark.use(caster);
                    return;
                }
            }
        }

        switch (abilityNumber){
            case 1:{
                torahSword.use(caster);
                return;
            }
            case 2:{
                divineGuidance.use(caster);
                return;
            }
            case 3:{
                reigningSword.use(caster);
                return;
            }
            case 4:{
                covenantSword.use(caster);
                return;
            }
            case 5:{
                orderShield.use(caster);
                return;
            }
            case 6:{
                gloryOfPaladins.use(caster);
                return;
            }
            case 7:{
                duranceOfTruth.use(caster);
                return;
            }
            case 8:{
                judgement.use(caster);
                return;
            }
        }
    }

    public void usePaladinUltimate(LivingEntity caster){

        SubClass subclass = profileManager.getAnyProfile(caster).getPlayerSubclass();

        switch (subclass){
            case Templar:{
                sanctityShield.use(caster);
                return;
            }
            case Divine:{
                representative.use(caster);
                return;
            }
            case Dawn:{
                lightWell.use(caster);
                return;
            }
        }
    }

    public void usePaladinBasic(LivingEntity caster){
        paladinBasic.useBasic(caster);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        if(subclass.equals(SubClass.Divine)){
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
                return covenantSword.getReadyIn(player);
            case 5:
                return orderShield.getCooldown(player);
            case 6:
                return gloryOfPaladins.getCooldown(player);
            case 7:
                return duranceOfTruth.getCooldown(player);
            case 8:
                return judgement.getReadyIn(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Templar:
                return sanctityShield.getSkillCooldown();
            case Divine:
                return representative.getSkillCooldown();
            case Dawn:
                return lightWell.getSkillCooldown();
        }

        return 0;
    }

    public int getPlayerUltimateCooldown(Player player){
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

        switch (subclass){
            case Templar:
                return sanctityShield.getPlayerCooldown(player);
            case Divine:
                return representative.getPlayerCooldown(player);
            case Dawn:
                return lightWell.getPlayerCooldown(player);
        }

        return 0;
    }

    public void resetCooldowns(LivingEntity caster){
        covenantSword.resetCooldown(caster);
        decreeHonor.resetCooldown(caster);
        divineGuidance.resetCooldown(caster);
        duranceOfTruth.resetCooldown(caster);
        gloryOfPaladins.resetCooldown(caster);
        honorCounter.resetCooldown(caster);
        judgement.resetCooldown(caster);
        justiceMark.resetCooldown(caster);
        lightWell.resetCooldown(caster);
        mercifulHealing.resetCooldown(caster);
        modestCalling.resetCooldown(caster);
        orderShield.resetCooldown(caster);
        reigningSword.resetCooldown(caster);
        representative.resetCooldown(caster);
        sacredAegis.resetCooldown(caster);
        sanctityShield.resetCooldown(caster);
        spiritualGift.resetCooldown(caster);
        torahSword.resetCooldown(caster);
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
    public PaladinBasic getPaladinBasic(){return paladinBasic;}
}
