package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

public class AbilityManager {

    private final ProfileManager profileManager;
    private final ElementalistAbilities elementalistAbilities;
    private final RangerAbilities rangerAbilities;
    private final CombatManager combatManager;

    public AbilityManager(Mystica main){
        profileManager = main.getProfileManager();
        combatManager = new CombatManager(main, this);
        elementalistAbilities = new ElementalistAbilities(main, this);
        rangerAbilities = new RangerAbilities(main, this);
    }

    public void useAbility(Player player, int abilityNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz;

        if(profileManager.getIfClassTrial(player)){
            clazz = profileManager.getTrialClass(player);
        }
        else{
            clazz = playerProfile.getPlayerClass();
        }


        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistAbility(player, abilityNumber);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerAbility(player, abilityNumber);
                return;
            }
        }
    }

    public void useBasic(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz;

        if(profileManager.getIfClassTrial(player)){
            clazz = profileManager.getTrialClass(player);
        }
        else{
            clazz = playerProfile.getPlayerClass();
        }

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistBasic(player);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerBasic(player);
                return;
            }
        }
    }

    public void useUltimate(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz;

        if(profileManager.getIfClassTrial(player)){
            clazz = profileManager.getTrialClass(player);
        }
        else{
            clazz = playerProfile.getPlayerClass();
        }

        switch (clazz.toLowerCase()){
            case "elementalist":{
                elementalistAbilities.useElementalistUltimate(player);
                return;
            }
            case "ranger":{
                rangerAbilities.useRangerUltimate(player);
                return;
            }
        }
    }

    public int getCooldown(Player player, int abilityNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz;

        if(profileManager.getIfClassTrial(player)){
            clazz = profileManager.getTrialClass(player);
        }
        else{
            clazz = playerProfile.getPlayerClass();
        }


        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case "ranger":{
                return rangerAbilities.getAbilityCooldown(player, abilityNumber);
            }
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        String clazz;

        if(profileManager.getIfClassTrial(player)){
            clazz = profileManager.getTrialClass(player);
        }
        else{
            clazz = playerProfile.getPlayerClass();
        }


        switch (clazz.toLowerCase()){
            case "elementalist":{
                return elementalistAbilities.getUltimateCooldown(player);
            }
            case "ranger":{
                return rangerAbilities.getUltimateCooldown(player);
            }
        }

        return 0;
    }

    public CombatManager getCombatManager(){
        return combatManager;
    }

    public ElementalistAbilities getElementalistAbilities(){return elementalistAbilities;}

    public RangerAbilities getRangerAbilities(){return rangerAbilities;}

    public void resetAbilityBuffs(Player player){

        elementalistAbilities.getFieryWing().removeInflame(player);

    }

}
