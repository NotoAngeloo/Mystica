package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Abilities.*;
import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;

    private final Map<LivingEntity, Boolean> castMap = new HashMap<>();
    private final Map<LivingEntity, Double> percentCastBar = new HashMap<>();

    //private final Map<Player, Boolean> skillRunning = new HashMap<>();

    private final NoneAbilities noneAbilities;
    private final ElementalistAbilities elementalistAbilities;
    private final RangerAbilities rangerAbilities;
    private final MysticAbilities mysticAbilities;
    private final ShadowKnightAbilities shadowKnightAbilities;
    private final PaladinAbilities paladinAbilities;
    private final WarriorAbilities warriorAbilities;
    private final AssassinAbilities assassinAbilities;

    private final AllSkillItems allSkillItems;

    private final CombatManager combatManager;

    public AbilityManager(Mystica main){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        allSkillItems = new AllSkillItems(main, this);
        combatManager = new CombatManager(main, this);

        noneAbilities = new NoneAbilities(main, this);
        elementalistAbilities = new ElementalistAbilities(main, this);
        rangerAbilities = new RangerAbilities(main, this);
        mysticAbilities = new MysticAbilities(main, this);
        shadowKnightAbilities = new ShadowKnightAbilities(main, this);
        paladinAbilities = new PaladinAbilities(main, this);
        warriorAbilities = new WarriorAbilities(main, this);
        assassinAbilities = new AssassinAbilities(main, this);
    }

    public void useAbility(LivingEntity caster, int abilityNumber){

        if(buffAndDebuffManager.getIfInterrupt(caster)){
            return;
        }

        if(getIfCasting(caster)){
            return;
        }


        interruptBasic(caster);

        Profile playerProfile = profileManager.getAnyProfile(caster);

        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistAbility(caster, abilityNumber);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerAbility(caster, abilityNumber);
                return;
            }
            case "mystic":{
                mysticAbilities.useMysticAbility(caster, abilityNumber);
                return;
            }
            case "shadow knight":{
                shadowKnightAbilities.useShadowKnightAbility(caster, abilityNumber);
                return;
            }
            case "paladin":{
                paladinAbilities.usePaladinAbility(caster, abilityNumber);
                return;
            }
            case "warrior":{
                warriorAbilities.useWarriorAbility(caster, abilityNumber);
                return;
            }
            case "assassin":{
                assassinAbilities.useAssassinAbility(caster, abilityNumber);
                return;
            }
            case "none":{
                noneAbilities.useNoneAbility(caster, abilityNumber);
                return;
            }
        }
    }

    public void useBasic(LivingEntity caster){

        if(getIfCasting(caster)){
            return;
        }

        if(buffAndDebuffManager.getIfCantAct(caster)){
            return;
        }


        Profile playerProfile = profileManager.getAnyProfile(caster);

        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistBasic(caster);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerBasic(caster);
                return;
            }
            case "mystic":{
                mysticAbilities.useMysticBasic(caster);
                return;
            }
            case "shadow knight":{
                shadowKnightAbilities.useShadowKnightBasic(caster);
                return;
            }
            case "paladin":{
                paladinAbilities.usePaladinBasic(caster);
                return;
            }
            case "warrior":{
                warriorAbilities.useWarriorBasic(caster);
                return;
            }
            case "assassin":{
                assassinAbilities.useAssassinBasic(caster);
                return;
            }
            case "none":{
                noneAbilities.useNoneBasic(caster);
                return;
            }
        }
    }

    public void useUltimate(LivingEntity caster){

        if(buffAndDebuffManager.getIfInterrupt(caster)){
            return;
        }


        if(getIfCasting(caster)){
            return;
        }


        Profile playerProfile = profileManager.getAnyProfile(caster);

        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistUltimate(caster);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerUltimate(caster);
                return;
            }
            case "mystic":{
                mysticAbilities.useMysticUltimate(caster);
                return;
            }
            case "shadow knight":{
                shadowKnightAbilities.useShadowKnightUltimate(caster);
                return;
            }
            case "paladin":{
                paladinAbilities.usePaladinUltimate(caster);
                return;
            }
            case "warrior":{
                warriorAbilities.useWarriorUltimate(caster);
                return;
            }
            case "assassin":{
                assassinAbilities.useAssassinUltimate(caster);
                return;
            }
        }
    }

    public int getCooldown(Player player, int abilityNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "ranger":{
                return rangerAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "mystic":{
                return mysticAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "shadow knight":{
                return shadowKnightAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "paladin":{
                return paladinAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "warrior":{
                return warriorAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "assassin":{
                return assassinAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "none":{
                return noneAbilities.getAbilityCooldown(player, abilityNumber);
            }
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz= playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistAbilities.getUltimateCooldown(player);
            }
            case "ranger":{
                return rangerAbilities.getUltimateCooldown(player);
            }
            case "mystic":{
                return mysticAbilities.getUltimateCooldown(player);
            }
            case "shadow knight":{
                return shadowKnightAbilities.getUltimateCooldown(player);
            }
            case "paladin":{
                return paladinAbilities.getUltimateCooldown(player);
            }
            case "warrior":{
                return warriorAbilities.getUltimateCooldown(player);
            }
            case "assassin":{
                return assassinAbilities.getUltimateCooldown(player);
            }
        }

        return 0;
    }

    public int getModelDataAddition(Player player, int abilityNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz = playerProfile.getPlayerClass();

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();


        switch (clazz.toLowerCase()){
            case "elementalist":
            case "shadow knight":
            case "paladin":
            case "ranger":
            case "warrior": {


                return 0;
            }
            case "mystic":{
                if(abilityNumber==-1){

                    if(subclass.equalsIgnoreCase("chaos")){
                        return mysticAbilities.getChaosMysticModelData(player);
                    }

                }


                return 0;
            }
            case "assassin":{


                if(abilityNumber==3){
                    return assassinAbilities.getWeaknessStrike().returnWhichItem(player);
                }

                if(abilityNumber==4){
                    return assassinAbilities.getPierce().returnWhichItem(player);
                }

                if(abilityNumber==8){
                    return assassinAbilities.getStealth().returnWhichItem(player);
                }

                if(abilityNumber==-1){

                    if(subclass.equalsIgnoreCase("duelist")){
                        return assassinAbilities.getDuelistsFrenzy().returnWhichItem(player);
                    }

                    return 0;
                }


                return 0;
            }
            case "none":{
                if(abilityNumber==8){
                    return noneAbilities.getAdrenaline().returnWhichItem(player);
                }
            }
        }


        return 0;
    }

    public CombatManager getCombatManager(){
        return combatManager;
    }
    public AllSkillItems getAllSkillItems(){return allSkillItems;}

    public ElementalistAbilities getElementalistAbilities(){return elementalistAbilities;}
    public RangerAbilities getRangerAbilities(){return rangerAbilities;}
    public MysticAbilities getMysticAbilities(){return mysticAbilities;}
    public ShadowKnightAbilities getShadowKnightAbilities(){return shadowKnightAbilities;}
    public PaladinAbilities getPaladinAbilities(){return paladinAbilities;}
    public WarriorAbilities getWarriorAbilities(){return warriorAbilities;}
    public AssassinAbilities getAssassinAbilities(){return assassinAbilities;}

    public void resetAbilityBuffs(LivingEntity caster){
        mysticAbilities.getEvilSpirit().removeShards(caster);
        mysticAbilities.getPurifyingBlast().unQueueInstantCast(caster);
        elementalistAbilities.getFieryWing().removeInflame(caster);
        shadowKnightAbilities.getInfection().removeEnhancement(caster);
        shadowKnightAbilities.getSoulReap().removeSoulMarks(caster);
        paladinAbilities.getDecision().removeDecision(caster);
        assassinAbilities.getCombo().removeAnAmountOfPoints(caster, assassinAbilities.getCombo().getComboPoints(caster));
    }

    public boolean getIfCasting(LivingEntity caster){
        return castMap.getOrDefault(caster, false);
    }
    public void setCasting(LivingEntity caster, boolean casting){
        castMap.put(caster, casting);
        if(caster instanceof Player){
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
        }

    }
    public void setCastBar(LivingEntity caster, double percent){

        if(!(caster instanceof Player)){
            return;
        }

        percentCastBar.put(caster, percent);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
    }
    public double getCastPercent(Player player){
        return percentCastBar.getOrDefault(player, 0.0);
    }

    //public boolean getIfSkillRunning(Player player){return skillRunning.getOrDefault(player, false);}
    //public void setSkillRunning(Player player, boolean running){skillRunning.put(player, running);}

    public void interruptBasic(LivingEntity caster){
        elementalistAbilities.getElementalistBasic().stopBasicRunning(caster);
        rangerAbilities.getRangerBasic().stopBasicRunning(caster);
        mysticAbilities.getMysticBasic().stopBasicRunning(caster);
        assassinAbilities.getAssassinBasic().stopBasicRunning(caster);
        paladinAbilities.getPaladinBasic().stopBasicRunning(caster);
        shadowKnightAbilities.getShadowKnightBasic().stopBasicRunning(caster);
        warriorAbilities.getWarriorBasic().stopBasicRunning(caster);
    }

    //change to entity later
    public void resetCooldowns(LivingEntity caster){
        assassinAbilities.resetCooldowns(caster);
        elementalistAbilities.resetCooldowns(caster);
        mysticAbilities.resetCooldowns(caster);
        noneAbilities.resetCooldowns(caster);
        paladinAbilities.resetCooldowns(caster);
        rangerAbilities.resetCooldowns(caster);
        shadowKnightAbilities.resetCooldowns(caster);
        warriorAbilities.resetCooldowns(caster);
    }

}
