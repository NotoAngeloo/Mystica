package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.CombatSystem.CombatManager;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {

    private final ProfileManager profileManager;
    private final StatusEffectManager statusEffectManager;

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
        statusEffectManager = main.getStatusEffectManager();
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

        if(!statusEffectManager.canCast(caster)){
            return;
        }

        if(getIfCasting(caster)){
            return;
        }

        interruptBasic(caster);

        combatManager.unSheathWeapon(caster);

        Profile playerProfile = profileManager.getAnyProfile(caster);

        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                elementalistAbilities.useElementalistAbility(caster, abilityNumber);
                return;
            }
            case Ranger -> {
                rangerAbilities.useRangerAbility(caster, abilityNumber);
                return;
            }
            case Mystic -> {
                mysticAbilities.useMysticAbility(caster, abilityNumber);
                return;
            }
            case Shadow_Knight -> {
                shadowKnightAbilities.useShadowKnightAbility(caster, abilityNumber);
                return;
            }
            case Paladin -> {
                paladinAbilities.usePaladinAbility(caster, abilityNumber);
                return;
            }
            case Warrior -> {
                warriorAbilities.useWarriorAbility(caster, abilityNumber);
                return;
            }
            case Assassin -> {
                assassinAbilities.useAssassinAbility(caster, abilityNumber);
                return;
            }
            case NONE -> {
                noneAbilities.useNoneAbility(caster, abilityNumber);
                return;
            }
        }
    }

    public void useBasic(LivingEntity caster){

        if(getIfCasting(caster)){
            return;
        }

        if(caster instanceof Player player){

            if(!player.getOpenInventory().getTitle().equalsIgnoreCase("crafting")){
                return;
            }
        }

        if(!statusEffectManager.canBasic(caster)){
            return;
        }

        combatManager.unSheathWeapon(caster);

        if(caster instanceof Player){
            if(profileManager.getAnyProfile(caster).getPlayerEquipment().getWeapon() == null){
                noneAbilities.useNoneBasic(caster);
                return;
            }
        }


        Profile playerProfile = profileManager.getAnyProfile(caster);

        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                elementalistAbilities.useElementalistBasic(caster);
                return;
            }
            case Ranger -> {
                rangerAbilities.useRangerBasic(caster);
                return;
            }
            case Mystic -> {
                mysticAbilities.useMysticBasic(caster);
                return;
            }
            case Shadow_Knight -> {
                shadowKnightAbilities.useShadowKnightBasic(caster);
                return;
            }
            case Paladin -> {
                paladinAbilities.usePaladinBasic(caster);
                return;
            }
            case Warrior -> {
                warriorAbilities.useWarriorBasic(caster);
                return;
            }
            case Assassin -> {
                assassinAbilities.useAssassinBasic(caster);
                return;
            }
            case NONE -> {
                noneAbilities.useNoneBasic(caster);
                return;
            }
        }
    }

    public void useUltimate(LivingEntity caster){

        if(!statusEffectManager.canCast(caster)){
            return;
        }


        if(getIfCasting(caster)){
            return;
        }

        combatManager.unSheathWeapon(caster);

        Profile playerProfile = profileManager.getAnyProfile(caster);

        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                elementalistAbilities.useElementalistUltimate(caster);
                return;
            }
            case Ranger -> {
                rangerAbilities.useRangerUltimate(caster);
                return;
            }
            case Mystic -> {
                mysticAbilities.useMysticUltimate(caster);
                return;
            }
            case Shadow_Knight -> {
                shadowKnightAbilities.useShadowKnightUltimate(caster);
                return;
            }
            case Paladin -> {
                paladinAbilities.usePaladinUltimate(caster);
                return;
            }
            case Warrior -> {
                warriorAbilities.useWarriorUltimate(caster);
                return;
            }
            case Assassin -> {
                assassinAbilities.useAssassinUltimate(caster);
                return;
            }
        }
    }

    public void resetResource(LivingEntity caster){

        Profile playerProfile = profileManager.getAnyProfile(caster);
        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                elementalistAbilities.getHeat().reduceHeat(caster, elementalistAbilities.getHeat().getHeat(caster));
                return;
            }
            case Ranger -> {
                rangerAbilities.getFocus().loseFocus(caster);
                return;
            }
            case Mystic -> {
                mysticAbilities.getMana().addManaToEntity(caster, 500);
                return;
            }
            case Shadow_Knight -> {
                shadowKnightAbilities.getEnergy().addEnergyToEntity(caster, 100);
                return;
            }
            case Paladin -> {
                return;
            }
            case Warrior -> {
                warriorAbilities.getRage().subTractRageFromEntity(caster, warriorAbilities.getRage().getCurrentRage(caster));
                return;
            }
            case Assassin -> {
                return;
            }
        }

    }

    public void incrementResource(LivingEntity caster){

        Profile playerProfile = profileManager.getAnyProfile(caster);
        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                elementalistAbilities.getHeat().loseHeatNaturally(caster);
                return;
            }
            case Ranger -> {
                rangerAbilities.getFocus().regenFocusNaturally(caster);
                return;
            }
            case Mystic -> {
                mysticAbilities.getMana().regenManaNaturally(caster);
                return;
            }
            case Shadow_Knight -> {
                shadowKnightAbilities.getEnergy().regenEnergyNaturally(caster);
                return;
            }
            case Paladin -> {
                return;
            }
            case Warrior -> {
                warriorAbilities.getRage().loseRageNaturally(caster);
                return;
            }
            case Assassin -> {
                return;
            }
        }
    }

    public int getCooldown(Player player, int abilityNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                return elementalistAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case Ranger -> {
                return rangerAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case Mystic -> {
                return mysticAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case Shadow_Knight -> {
                return shadowKnightAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case Paladin -> {
                return paladinAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case Warrior -> {
                return warriorAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case Assassin -> {
                return assassinAbilities.getAbilityCooldown(player, abilityNumber);
            }
            case NONE -> {
                return noneAbilities.getAbilityCooldown(player, abilityNumber);
            }
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                return elementalistAbilities.getUltimateCooldown(player);
            }
            case Ranger -> {
                return rangerAbilities.getUltimateCooldown(player);
            }
            case Mystic -> {
                return mysticAbilities.getUltimateCooldown(player);
            }
            case Shadow_Knight -> {
                return shadowKnightAbilities.getUltimateCooldown(player);
            }
            case Paladin -> {
                return paladinAbilities.getUltimateCooldown(player);
            }
            case Warrior -> {
                return warriorAbilities.getUltimateCooldown(player);
            }
            case Assassin -> {
                return assassinAbilities.getUltimateCooldown(player);
            }
        }

        return 0;
    }

    public int getPlayerUltimateCooldown(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                return elementalistAbilities.getPlayerUltimateCooldown(player);
            }
            case Ranger -> {
                return rangerAbilities.getPlayerUltimateCooldown(player);
            }
            case Mystic -> {
                return mysticAbilities.getPlayerUltimateCooldown(player);
            }
            case Shadow_Knight -> {
                return shadowKnightAbilities.getPlayerUltimateCooldown(player);
            }
            case Paladin -> {
                return paladinAbilities.getPlayerUltimateCooldown(player);
            }
            case Warrior -> {
                return warriorAbilities.getPlayerUltimateCooldown(player);
            }
            case Assassin -> {
                return assassinAbilities.getPlayerUltimateCooldown(player);
            }
        }

        return 0;
    }

    public int getModelDataAddition(Player player, int abilityNumber){

        Profile playerProfile = profileManager.getAnyProfile(player);

        PlayerClass clazz = playerProfile.getPlayerClass();
        SubClass subclass = profileManager.getAnyProfile(player).getPlayerSubclass();


        switch (clazz) {
            case Elementalist, Paladin, Ranger -> {

                return 0;
            }
            case Shadow_Knight -> {

                if (abilityNumber == 2) {
                    return shadowKnightAbilities.getSpiritualAttack().returnWhichItem(player);
                }

                if (abilityNumber == 4) {
                    return shadowKnightAbilities.getBloodsucker().returnWhichItem(player);
                }

                if (abilityNumber == 5) {
                    return shadowKnightAbilities.getSoulReap().returnWhichItem(player);
                }

                if (abilityNumber == 6) {
                    return shadowKnightAbilities.getShadowGrip().returnWhichItem(player);
                }

                if (abilityNumber == -1) {

                    if (subclass.equals(SubClass.Blood)) {
                        return shadowKnightAbilities.getBloodShield().returnWhichItem(player);
                    }

                    if (subclass.equals(SubClass.Doom)) {
                        return shadowKnightAbilities.getAnnihilation().returnWhichItem(player);
                    }

                }

                return 0;
            }
            case Warrior -> {

                if (abilityNumber == 4) {
                    return warriorAbilities.getMeteorCrater().returnWhichItem(player);
                }

                return 0;
            }
            case Mystic -> {

                if (subclass.equals(SubClass.Chaos)) {

                    if (abilityNumber == -1) {
                        return mysticAbilities.getChaosMysticModelData(player);
                    }


                }

                if (!subclass.equals(SubClass.Chaos)) {

                    if (abilityNumber == 1) {
                        return mysticAbilities.getArcaneShield().returnWhichItem(player);
                    }

                    if (abilityNumber == 2) {
                        return mysticAbilities.getPurifyingBlast().returnWhichItem(player);
                    }

                    if (abilityNumber == 6) {
                        return mysticAbilities.getAurora().returnWhichItem(player);
                    }

                    if (abilityNumber == 7) {
                        return mysticAbilities.getArcaneContract().returnWhichItem(player);
                    }

                    if (abilityNumber == 8) {
                        return mysticAbilities.getLightSigil().returnWhichItem(player);
                    }

                    if (subclass.equals(SubClass.Shepard)) {
                        if (abilityNumber == -1) {
                            return mysticAbilities.getEnlightenment().returnWhichItem(player);
                        }
                    }


                }


                return 0;
            }
            case Assassin -> {


                if (abilityNumber == 3) {
                    return assassinAbilities.getWeaknessStrike().returnWhichItem(player);
                }

                if (abilityNumber == 4) {
                    return assassinAbilities.getPierce().returnWhichItem(player);
                }

                if (abilityNumber == 8) {
                    return assassinAbilities.getStealth().returnWhichItem(player);
                }

                if (abilityNumber == -1) {

                    if (subclass.equals(SubClass.Duelist)) {
                        return assassinAbilities.getDuelistsFrenzy().returnWhichItem(player);
                    }

                    return 0;
                }


                return 0;
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
        mysticAbilities.getConsolation().removeTargets(caster);
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
        if(caster instanceof Player player){
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
        }

    }
    public void setCastBar(LivingEntity caster, double percent){

        if(!(caster instanceof Player player)){
            return;
        }

        percentCastBar.put(caster, percent);
        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Cast));
    }
    public double getCastPercent(Player player){
        return percentCastBar.getOrDefault(player, 0.0);
    }


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

    /*public void hideFromPlayers(LivingEntity armorStand){
        List<Player> players = armorStand.getWorld().getPlayers();
        for(Player player : players){
            player.hideEntity(Mystica.getPlugin(), armorStand);
        }
    }*/

}
