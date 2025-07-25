package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.Inventories.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class FakePlayerProfile implements Profile {

    private Boolean ifDead;
    private Boolean ifInCombat;

    private double currentHealth;

    private Stats stats;

    private PlayerClass playerClass;
    private SubClass playerSubclass;

    private ItemStack[] savedInv;

    public FakePlayerProfile(
            Boolean ifDead,
            Boolean ifInCombat,

            Stats stats,

            PlayerClass playerClass,
            SubClass playerSubclass


    ) {

        this.ifDead = ifDead;
        this.ifInCombat = ifInCombat;


        this.stats = stats;

        this.playerClass = playerClass;
        this.playerSubclass = playerSubclass;

    }

    @Override
    public Boolean getIfDead(){return ifDead;}
    @Override
    public Boolean getIfInCombat(){return ifInCombat;}

    @Override
    public void setIfDead(Boolean ifDead) {
        this.ifDead = ifDead;
    }
    @Override
    public void setIfInCombat(Boolean ifInCombat) {
        this.ifInCombat = ifInCombat;
    }
    @Override
    public double getCurrentHealth(){return currentHealth;}
    @Override
    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = currentHealth;
    }


    @Override
    public void setLevelStats(int level, String subclass){}


    @Override
    public Stats getStats() {
        return stats;
    }

    @Override
    public StatsFromGear getGearStats() {
        return null;
    }


    @Override
    public void setStats(Stats stats) {
        this.stats = stats;
    }

    @Override
    public void setGearStats(StatsFromGear statsFromGear) {

    }

    @Override
    public int getTotalHealth() {
        return stats.getHealth();
    }


    @Override
    public int getTotalAttack() {
        return stats.getAttack();
    }

    @Override
    public int getTotalDefense() {
        return stats.getDefense();
    }

    @Override
    public int getTotalMagicDefense() {
        return stats.getMagic_Defense();
    }

    @Override
    public int getTotalCrit() {
        return stats.getCrit();
    }

    @Override
    public PlayerClass getPlayerClass(){
        return playerClass;
    }
    @Override
    public void setPlayerClass(PlayerClass playerClass){
        this.playerClass = playerClass;
    }
    @Override
    public SubClass getPlayerSubclass(){return playerSubclass;}
    @Override
    public void setPlayerSubclass(SubClass subclass){
        this.playerSubclass = subclass;
    }

    @Override
    public MysticaBagCollection getMysticaBagCollection() {
        return null;
    }


    @Override
    public PlayerEquipment getPlayerEquipment() {
        return null;
    }

    @Override
    public PlayerBossLevel getPlayerBossLevel() {
        return null;
    }

    @Override
    public Skill_Level getSkillLevels() {
        return new Skill_Level(0,0,0,0,0,0,0,0);
    }

    @Override
    public EquipSkills getEquipSkills() {
        return null;
    }


    @Override
    public Boolean getIsPassive() {
        return true;
    }

    @Override
    public Boolean getIsMovable() {
        return true;
    }

    @Override
    public Boolean getImmortality() {
        return false;
    }

    @Override
    public Boolean getIfObject() {
        return false;
    }

    @Override
    public Yield getYield() {
        return null;
    }

    @Override
    public Boolean fakePlayer() {
        return true;
    }

    @Override
    public Milestones getMilestones() {
        return null;
    }


    @Override
    public void getVoidsOnDeath(Set<Player> players) {

    }

}
