package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Abilities.*;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShieldAbilityManaDisplayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityManager {

    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;

    private final Map<Player, Boolean> castMap = new HashMap<>();
    private final Map<Player, Double> percentCastBar = new HashMap<>();
    private final Map<UUID, Long> globalCooldown = new HashMap<>();

    private final Map<Player, Boolean> skillRunning = new HashMap<>();

    private final ElementalistAbilities elementalistAbilities;
    private final RangerAbilities rangerAbilities;
    private final MysticAbilities mysticAbilities;
    private final ShadowKnightAbilities shadowKnightAbilities;
    private final PaladinAbilities paladinAbilities;
    private final WarriorAbilities warriorAbilities;
    private final AssassinAbilities assassinAbilities;

    private final CombatManager combatManager;

    public AbilityManager(Mystica main){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = new CombatManager(main, this);

        elementalistAbilities = new ElementalistAbilities(main, this);
        rangerAbilities = new RangerAbilities(main, this);
        mysticAbilities = new MysticAbilities(main, this);
        shadowKnightAbilities = new ShadowKnightAbilities(main, this);
        paladinAbilities = new PaladinAbilities(main, this);
        warriorAbilities = new WarriorAbilities(main, this);
        assassinAbilities = new AssassinAbilities(main, this);
    }

    public void useAbility(Player player, int abilityNumber){

        if(buffAndDebuffManager.getIfInterrupt(player)){
            return;
        }

        if(globalCooldown.get(player.getUniqueId()) == null){
            globalCooldown.put(player.getUniqueId(), (System.currentTimeMillis() / 1000) - 1);
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if(currentTime - globalCooldown.get(player.getUniqueId()) < 1){
            return;
        }
        globalCooldown.put(player.getUniqueId(), currentTime);

        if(getIfCasting(player)){
            return;
        }

        if(getIfSkillRunning(player)){
            return;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistAbility(player, abilityNumber);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerAbility(player, abilityNumber);
                return;
            }
            case "mystic":{
                mysticAbilities.useMysticAbility(player, abilityNumber);
                return;
            }
            case "shadow knight":{
                shadowKnightAbilities.useShadowKnightAbility(player, abilityNumber);
                return;
            }
            case "paladin":{
                paladinAbilities.usePaladinAbility(player, abilityNumber);
                return;
            }
            case "warrior":{
                warriorAbilities.useWarriorAbility(player, abilityNumber);
                return;
            }
            case "assassin":{
                assassinAbilities.useAssassinAbility(player, abilityNumber);
                return;
            }
        }
    }

    public void useBasic(Player player){

        if(getIfCasting(player)){
            return;
        }

        if(getIfSkillRunning(player)){
            return;
        }

        if(buffAndDebuffManager.getIfCantAct(player)){
            return;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz = playerProfile.getPlayerClass();


        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistBasic(player);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerBasic(player);
                return;
            }
            case "mystic":{
                mysticAbilities.useMysticBasic(player);
                return;
            }
            case "shadow knight":{
                shadowKnightAbilities.useShadowKnightBasic(player);
                return;
            }
            case "paladin":{
                paladinAbilities.usePaladinBasic(player);
                return;
            }
            case "warrior":{
                warriorAbilities.useWarriorBasic(player);
                return;
            }
            case "assassin":{
                assassinAbilities.useAssassinBasic(player);
                return;
            }
        }
    }

    public void useUltimate(Player player){

        if(buffAndDebuffManager.getIfInterrupt(player)){
            return;
        }

        if(globalCooldown.get(player.getUniqueId()) == null){
            globalCooldown.put(player.getUniqueId(), (System.currentTimeMillis() / 1000) - 1);
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if(currentTime - globalCooldown.get(player.getUniqueId()) < 1){
            return;
        }
        globalCooldown.put(player.getUniqueId(), currentTime);

        if(getIfCasting(player)){
            return;
        }

        if(getIfSkillRunning(player)){
            return;
        }

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz = playerProfile.getPlayerClass();

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistUltimate(player);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerUltimate(player);
                return;
            }
            case "mystic":{
                mysticAbilities.useMysticUltimate(player);
                return;
            }
            case "shadow knight":{
                shadowKnightAbilities.useShadowKnightUltimate(player);
                return;
            }
            case "paladin":{
                paladinAbilities.usePaladinUltimate(player);
                return;
            }
            case "warrior":{
                warriorAbilities.useWarriorUltimate(player);
                return;
            }
            case "assassin":{
                assassinAbilities.useAssassinUltimate(player);
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

        int cooldown;

        switch (clazz.toLowerCase()){
            case "elementalist":{

                if(abilityNumber==-1){
                    cooldown = elementalistAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = elementalistAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
            case "ranger":{
                if(abilityNumber==-1){
                    cooldown = rangerAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = rangerAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
            case "mystic":{
                if(abilityNumber==-1){

                    if(subclass.equalsIgnoreCase("chaos")){
                        return mysticAbilities.getChaosMysticModelData(player);
                    }

                    cooldown = mysticAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = mysticAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
            case "shadow knight":{
                if(abilityNumber==-1){
                    cooldown = shadowKnightAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = shadowKnightAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
            case "paladin":{
                if(abilityNumber==-1){
                    cooldown = paladinAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = paladinAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
            case "warrior":{
                if(abilityNumber==-1){
                    cooldown = warriorAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = warriorAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
            case "assassin":{

                //TODO: unfuck this up when i have the combo specific model data items

                if(abilityNumber==-1){
                    cooldown = assassinAbilities.getUltimateCooldown(player);
                }
                else{
                    cooldown = assassinAbilities.getAbilityCooldown(player, abilityNumber);
                }

                if(cooldown == 1){
                    return 1;
                }

                return 0;
            }
        }


        return 0;
    }

    public CombatManager getCombatManager(){
        return combatManager;
    }

    public ElementalistAbilities getElementalistAbilities(){return elementalistAbilities;}
    public RangerAbilities getRangerAbilities(){return rangerAbilities;}
    public MysticAbilities getMysticAbilities(){return mysticAbilities;}
    public ShadowKnightAbilities getShadowKnightAbilities(){return shadowKnightAbilities;}
    public PaladinAbilities getPaladinAbilities(){return paladinAbilities;}
    public WarriorAbilities getWarriorAbilities(){return warriorAbilities;}
    public AssassinAbilities getAssassinAbilities(){return assassinAbilities;}

    public void resetAbilityBuffs(Player player){
        mysticAbilities.getEvilSpirit().removeShards(player);
        mysticAbilities.getPurifyingBlast().unQueueInstantCast(player);
        elementalistAbilities.getFieryWing().removeInflame(player);
        shadowKnightAbilities.getInfection().removeEnhancement(player);
        shadowKnightAbilities.getSoulReap().removeSoulMarks(player);
        paladinAbilities.getDecision().removeDecision(player);
        assassinAbilities.getCombo().removeAnAmountOfPoints(player, assassinAbilities.getCombo().getComboPoints(player));
    }

    public boolean getIfCasting(Player player){
        return castMap.getOrDefault(player, false);
    }
    public void setCasting(Player player, boolean casting){
        castMap.put(player, casting);
    }
    public void setCastBar(Player player, double percent){
        percentCastBar.put(player, percent);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player, false));
    }
    public double getCastPercent(Player player){
        return percentCastBar.getOrDefault(player, 0.0);
    }

    public boolean getIfSkillRunning(Player player){return skillRunning.getOrDefault(player, false);}
    public void setSkillRunning(Player player, boolean running){skillRunning.put(player, running);}

}
