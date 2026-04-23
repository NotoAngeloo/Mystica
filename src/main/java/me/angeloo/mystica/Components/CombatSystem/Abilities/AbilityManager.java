package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.*;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Assassin.Combo;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Elementalist.Heat;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic.Mana;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin.Purity;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Ranger.Focus;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.ShadowKnight.Energy;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Warrior.Rage;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
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
    private final CooldownManager cooldownManager;
    private final StatusEffectManager statusEffectManager;
    private final PlayerStateManager playerStateManager;
    private final AbilityResolver abilityResolver;
    private final AbilityMarkManager abilityMarkManager;

    private final Map<LivingEntity, Boolean> castMap = new HashMap<>();
    private final Map<LivingEntity, Double> percentCastBar = new HashMap<>();

    //private final Map<Player, Boolean> skillRunning = new HashMap<>();

    private final Combo combo;
    private final Heat heat;
    private final Mana mana;
    private final Purity purity;
    private final Focus focus;
    private final Energy energy;
    private final Rage rage;

    //private final NoneAbilities noneAbilities;
    //private final ElementalistAbilities elementalistAbilities;
    //private final RangerAbilities rangerAbilities;
    //private final MysticAbilities mysticAbilities;
    //private final ShadowKnightAbilities shadowKnightAbilities;
    //private final PaladinAbilities paladinAbilities;
    //private final WarriorAbilities warriorAbilities;
    //private final AssassinAbilities assassinAbilities;

    //private final AllSkillItems allSkillItems;

    private final CombatManager combatManager;

    public AbilityManager(Mystica main){
        profileManager = main.getProfileManager();
        cooldownManager = new CooldownManager();
        statusEffectManager = main.getStatusEffectManager();
        playerStateManager = new PlayerStateManager();
        abilityMarkManager = new AbilityMarkManager(main);


        //allSkillItems = new AllSkillItems(main, this);
        combatManager = new CombatManager(main, this);
        //put resources here
        combo = new Combo(main);
        heat = new Heat(main);
        mana = new Mana(main);
        focus = new Focus(main);
        rage = new Rage(main);
        energy = new Energy(main);
        purity = new Purity(main);
        abilityResolver = new AbilityResolver(main, this);

        //noneAbilities = new NoneAbilities(main, this);
        //elementalistAbilities = new ElementalistAbilities(main, this);
        //rangerAbilities = new RangerAbilities(main, this);
        //mysticAbilities = new MysticAbilities(main, this);
        //shadowKnightAbilities = new ShadowKnightAbilities(main, this);
        //paladinAbilities = new PaladinAbilities(main, this);
        //warriorAbilities = new WarriorAbilities(main, this);
        //assassinAbilities = new AssassinAbilities(main, this);
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

        long now = System.currentTimeMillis();

        if(cooldownManager.isOnGlobalCooldown(caster.getUniqueId(), now)){
            return;
        }

        PlayerClass clazz = playerProfile.getPlayerClass();
        SubClass subClass = playerProfile.getPlayerSubclass();

        Ability ability = abilityResolver.resolve(clazz, subClass, abilityNumber);

        if(ability == null){
            return;
        }

        //perhaps in future make this "AbilityResult" enum to explain *why* failed, but that is out of scope atm

        boolean success = ability.use(caster);

        if(!success){
            return;
        }

        int gcd = ability.getGlobalCooldownMillis();

        if(gcd > 0){
            cooldownManager.applyGlobalCooldown(caster.getUniqueId(), statusEffectManager.getHastePercent(caster), now, gcd);
        }

        /*switch (clazz) {
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
        }*/
    }

    public void useBasic(LivingEntity caster){

        if(getIfCasting(caster)){
            return;
        }

        /*if(caster instanceof Player player){

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
        }*/


        Profile playerProfile = profileManager.getAnyProfile(caster);

        PlayerClass clazz = playerProfile.getPlayerClass();

        /*switch (clazz) {
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
        }*/
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

        /*switch (clazz) {
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
        }*/
    }

    public void resetResource(LivingEntity caster){

        Profile playerProfile = profileManager.getAnyProfile(caster);
        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                heat.reduceHeat(caster, heat.getHeat(caster));
                return;
            }
            case Ranger -> {
                focus.loseFocus(caster);
                return;
            }
            case Mystic -> {
                mana.addManaToEntity(caster, 500);
                return;
            }
            case Shadow_Knight -> {
                energy.addEnergyToEntity(caster, 100);
                return;
            }
            case Paladin -> {
                purity.reset(caster);
                return;
            }
            case Warrior -> {
                rage.subTractRageFromEntity(caster, rage.getCurrentRage(caster));
                return;
            }
            case Assassin -> {
                combo.removeAnAmountOfPoints(caster, combo.getComboPoints(caster));
                return;
            }
        }

    }

    public void incrementResource(LivingEntity caster){

        Profile playerProfile = profileManager.getAnyProfile(caster);
        PlayerClass clazz = playerProfile.getPlayerClass();

        switch (clazz) {
            case Elementalist -> {
                heat.loseHeatNaturally(caster);
                return;
            }
            case Ranger -> {
                focus.regenFocusNaturally(caster);
                return;
            }
            case Mystic -> {
                mana.regenManaNaturally(caster);
                return;
            }
            case Shadow_Knight -> {
                energy.regenEnergyNaturally(caster);
                return;
            }
            case Paladin -> {
                return;
            }
            case Warrior -> {
                rage.loseRageNaturally(caster);
                return;
            }
            case Assassin -> {
                return;
            }
        }
    }




    /*public int getModelDataAddition(Player player, int abilityNumber){

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
    }*/

    public CombatManager getCombatManager(){
        return combatManager;
    }

    /*public ElementalistAbilities getElementalistAbilities(){return elementalistAbilities;}
    public RangerAbilities getRangerAbilities(){return rangerAbilities;}
    public MysticAbilities getMysticAbilities(){return mysticAbilities;}
    public ShadowKnightAbilities getShadowKnightAbilities(){return shadowKnightAbilities;}
    public PaladinAbilities getPaladinAbilities(){return paladinAbilities;}
    public WarriorAbilities getWarriorAbilities(){return warriorAbilities;}
    public AssassinAbilities getAssassinAbilities(){return assassinAbilities;}*/

    public void resetAbilityBuffs(LivingEntity caster){
        /*mysticAbilities.getEvilSpirit().removeShards(caster);
        mysticAbilities.getPurifyingBlast().unQueueInstantCast(caster);
        mysticAbilities.getConsolation().removeTargets(caster);
        elementalistAbilities.getFieryWing().removeInflame(caster);
        shadowKnightAbilities.getInfection().removeEnhancement(caster);
        shadowKnightAbilities.getSoulReap().removeSoulMarks(caster);
        paladinAbilities.getDecision().removeDecision(caster);
        assassinAbilities.getCombo().removeAnAmountOfPoints(caster, assassinAbilities.getCombo().getComboPoints(caster));*/
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
        /*elementalistAbilities.getElementalistBasic().stopBasicRunning(caster);
        rangerAbilities.getRangerBasic().stopBasicRunning(caster);
        mysticAbilities.getMysticBasic().stopBasicRunning(caster);
        assassinAbilities.getAssassinBasic().stopBasicRunning(caster);
        paladinAbilities.getPaladinBasic().stopBasicRunning(caster);
        shadowKnightAbilities.getShadowKnightBasic().stopBasicRunning(caster);
        warriorAbilities.getWarriorBasic().stopBasicRunning(caster);*/
    }


    public CooldownManager getCooldownManager(){
        return cooldownManager;
    }

    public PlayerStateManager getPlayerStateManager(){
        return playerStateManager;
    }

    public AbilityResolver getAbilityResolver(){
        return abilityResolver;
    }

    public Combo getCombo(){
        return combo;
    }
    public Heat getHeat() {
        return heat;
    }

    public Mana getMana() {
        return mana;
    }

    public Purity getPurity() {
        return purity;
    }

    public Focus getFocus() {
        return focus;
    }

    public Energy getEnergy() {
        return energy;
    }

    public Rage getRage() {
        return rage;
    }

    public AbilityMarkManager getAbilityMarkManager() {
        return abilityMarkManager;
    }

    /*public void hideFromPlayers(LivingEntity armorStand){
        List<Player> players = armorStand.getWorld().getPlayers();
        for(Player player : players){
            player.hideEntity(Mystica.getPlugin(), armorStand);
        }
    }*/

}
