package me.angeloo.mystica.Components.Creatures;

import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MadDummy {

    private final ProfileManager profileManager;

    public MadDummy(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void makeProfile(UUID uuid){

        int level = 1;
        int attack = 50;
        int health = 500;
        int mana = 0;
        int defense = 50;
        int magic_defense = 50;
        int crit = 0;

        Stats stats = new Stats(level,attack,health,mana,defense,magic_defense,crit);

        Boolean isMovable = true;
        Boolean immortal = false;
        Boolean object = false;
        Boolean passive = false;
        Yield yield = new Yield(0.0f, null);

        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(health, stats, isMovable, immortal, passive, object, yield) {

            @Override
            public Bal getBal() {
                return null;
            }

            @Override
            public Boolean getIfDead() {
                return null;
            }

            @Override
            public Boolean getIfInCombat() {
                return null;
            }

            @Override
            public void setIfDead(Boolean ifDead) {

            }

            @Override
            public void setIfInCombat(Boolean ifInCombat) {

            }

            @Override
            public double getCurrentMana() {
                return 0;
            }

            @Override
            public void setCurrentMana(double currentMana) {

            }

            @Override
            public void setLevelStats(int level, String subclass) {

            }

            @Override
            public StatsFromGear getGearStats() {
                return null;
            }


            @Override
            public void setGearStats(StatsFromGear statsFromGear) {

            }

            @Override
            public int getTotalHealth() {
                return health;
            }

            @Override
            public int getTotalMana() {
                return mana;
            }

            @Override
            public int getTotalAttack() {
                return attack;
            }

            @Override
            public int getTotalDefense() {
                return defense;
            }

            @Override
            public int getTotalMagicDefense() {
                return magic_defense;
            }

            @Override
            public int getTotalCrit() {
                return crit;
            }

            @Override
            public String getPlayerClass() {
                return null;
            }

            @Override
            public void setPlayerClass(String playerClass) {

            }

            @Override
            public String getPlayerSubclass() {
                return null;
            }

            @Override
            public void setPlayerSubclass(String playerSubclass) {

            }

            @Override
            public ItemStack[] getSavedInv() {
                return new ItemStack[0];
            }

            @Override
            public void setSavedInv(ItemStack[] inv) {

            }

            @Override
            public void removeSavedInv() {

            }

            @Override
            public PlayerBag getPlayerBag() {
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
                return null;
            }

            @Override
            public EquipSkills getEquipSkills() {
                return null;
            }

            @Override
            public Milestones getMilestones() {
                return null;
            }

            @Override
            public void getVoidsOnDeath() {

            }
        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);


    }

}
