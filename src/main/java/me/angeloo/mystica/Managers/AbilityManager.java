package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.Components.Abilities.MysticAbilities;
import me.angeloo.mystica.Components.Abilities.RangerAbilities;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Mystica;
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

    private final ElementalistAbilities elementalistAbilities;
    private final RangerAbilities rangerAbilities;
    private final MysticAbilities mysticAbilities;
    private final CombatManager combatManager;

    public AbilityManager(Mystica main){
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = new CombatManager(main, this);
        elementalistAbilities = new ElementalistAbilities(main, this);
        rangerAbilities = new RangerAbilities(main, this);
        mysticAbilities = new MysticAbilities(main, this);
    }

    public void useAbility(Player player, int abilityNumber){

        if(buffAndDebuffManager.getIfCantAct(player)){
            return;
        }

        if(globalCooldown.get(player.getUniqueId()) == null){
            globalCooldown.put(player.getUniqueId(), (System.currentTimeMillis() / 1000) - 1);
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if(currentTime - globalCooldown.get(player.getUniqueId()) < .5){
            return;
        }
        globalCooldown.put(player.getUniqueId(), currentTime);

        if(getIfCasting(player)){
            return;
        }

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
            case "mystic":{
                mysticAbilities.useMysticAbility(player, abilityNumber);
                return;
            }
        }
    }

    public void useBasic(Player player){

        if(getIfCasting(player)){
            return;
        }

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
            case "mystic":{
                mysticAbilities.useMysticBasic(player);
                return;
            }
        }
    }

    public void useUltimate(Player player){

        if(globalCooldown.get(player.getUniqueId()) == null){
            globalCooldown.put(player.getUniqueId(), (System.currentTimeMillis() / 1000) - 1);
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if(currentTime - globalCooldown.get(player.getUniqueId()) < .5){
            return;
        }
        globalCooldown.put(player.getUniqueId(), currentTime);

        if(getIfCasting(player)){
            return;
        }

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
            case "mystic":{
                mysticAbilities.useMysticUltimate(player);
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
            case "mystic":{
                return mysticAbilities.getAbilityCooldown(player, abilityNumber);
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
            case "mystic":{
                return mysticAbilities.getUltimateCooldown(player);
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

    public void resetAbilityBuffs(Player player){
        mysticAbilities.getEvilSpirit().removeShards(player);
        elementalistAbilities.getFieryWing().removeInflame(player);

    }

    public boolean getIfCasting(Player player){
        return castMap.getOrDefault(player, false);
    }
    public void setCasting(Player player, boolean casting){
        castMap.put(player, casting);
    }
    public void setCastBar(Player player, double percent){
        percentCastBar.put(player, percent);
    }
    public double getCastPercent(Player player){
        return percentCastBar.getOrDefault(player, 0.0);
    }

}
