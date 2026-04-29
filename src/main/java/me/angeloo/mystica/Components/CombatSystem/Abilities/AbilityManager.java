package me.angeloo.mystica.Components.CombatSystem.Abilities;

import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackEngine;
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
import java.util.UUID;

public class AbilityManager {

    private final ProfileManager profileManager;
    private final CooldownManager cooldownManager;
    private final StatusEffectManager statusEffectManager;
    private final PlayerStateManager playerStateManager;
    private final AbilityResolver abilityResolver;
    private final AbilityMarkManager abilityMarkManager;
    private final BasicAttackEngine basicAttackEngine;

    private final Map<UUID, Double> percentCastBar = new HashMap<>();
    private final Map<UUID, String> skillCurrentlyCasting = new HashMap<>();


    private final Combo combo;
    private final Heat heat;
    private final Mana mana;
    private final Purity purity;
    private final Focus focus;
    private final Energy energy;
    private final Rage rage;


    private final CombatManager combatManager;

    public AbilityManager(Mystica main){
        profileManager = main.getProfileManager();
        cooldownManager = main.getCooldownManager();
        basicAttackEngine = new BasicAttackEngine(main);
        statusEffectManager = main.getStatusEffectManager();
        playerStateManager = new PlayerStateManager();
        abilityMarkManager = new AbilityMarkManager(main);


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

    }

    public void useBasic(LivingEntity caster){

        if(getIfCasting(caster)){
            return;
        }

        long now = System.currentTimeMillis();

        if(cooldownManager.isOnGlobalCooldown(caster.getUniqueId(), now)){
            return;
        }

        Profile playerProfile = profileManager.getAnyProfile(caster);

        PlayerClass clazz = playerProfile.getPlayerClass();

       SubClass subClass = playerProfile.getPlayerSubclass();

        BasicAttackDefinition basic = abilityResolver.resolveBasic(clazz, subClass);

        if(basic == null){
            return;
        }

        basicAttackEngine.start(caster, basic);
    }

    public void useUltimate(LivingEntity caster){

        if(!statusEffectManager.canCast(caster)){
            return;
        }

        if(getIfCasting(caster)){
            return;
        }

        combatManager.unSheathWeapon(caster);

        interruptBasic(caster);

        Profile playerProfile = profileManager.getAnyProfile(caster);

        long now = System.currentTimeMillis();

        SubClass subClass = playerProfile.getPlayerSubclass();

        Ability ability = abilityResolver.resolveUltimate(subClass);

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


    public CombatManager getCombatManager(){
        return combatManager;
    }


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

        if(percentCastBar.containsKey(caster.getUniqueId())){
            return percentCastBar.get(caster.getUniqueId()) > 0;
        }
        return false;
    }

    public void setCastBar(LivingEntity caster, double percent){
        percentCastBar.put(caster.getUniqueId(), percent);
        Bukkit.getPluginManager().callEvent(new HudUpdateEvent(caster, BarType.Cast));
    }
    public double getCastPercent(Player player){
        return percentCastBar.getOrDefault(player.getUniqueId(), 0.0);
    }
    public void stopCasting(LivingEntity caster){
        setCastBar(caster, 0);
        Bukkit.getPluginManager().callEvent(new HudUpdateEvent(caster, BarType.Cast));
    }
    public String getSkillCurrentlyCasting(LivingEntity caster){
        return skillCurrentlyCasting.getOrDefault(caster.getUniqueId(), "");
    }
    public void setSkillCurrentlyCasting(LivingEntity caster, String skillIcon){
        skillCurrentlyCasting.put(caster.getUniqueId(), skillIcon);
    }

    public void interruptBasic(LivingEntity caster){
        basicAttackEngine.stop(caster);
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
