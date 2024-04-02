package me.angeloo.mystica.Components.Creatures;

import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class BetaTester {

    private final ProfileManager profileManager;

    public BetaTester(Mystica main){
        profileManager = main.getProfileManager();
    }

    public void makeProfile(UUID uuid){

        int level = 1;
        int at = 60;
        int hp = 257;
        int mana = 0;
        int regen = 0;
        int manaregen = 0;
        int def = 60;
        int mdef = 60;
        int crit = 0;

        Stats stats = new Stats(level, at, hp, mana, regen, manaregen, def, mdef, crit);
        Boolean isMovable = true;
        Boolean immmortal = false;
        Boolean object = false;
        Boolean passive = false;

        float xpYield = 0f;

        Yield yield = new Yield(xpYield, new ArrayList<>());

        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(hp, stats, isMovable, immmortal, passive, object, yield) {

            @Override
            public Bal getBal() {
                return null;
            }

            @Override
            public Boolean getIfDead() {
                return false;
            }

            @Override
            public Boolean getIfInCombat() {
                return false;
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
                return getStats().getHealth();
            }

            @Override
            public int getTotalMana() {
                return getStats().getMana();
            }

            @Override
            public int getTotalAttack() {
                return getStats().getAttack();
            }

            @Override
            public int getTotalDefense() {
                return getStats().getDefense();
            }

            @Override
            public int getTotalMagicDefense() {
                return getStats().getMagic_Defense();
            }

            @Override
            public int getTotalRegen() {
                return getStats().getRegen();
            }

            @Override
            public int getTotalManaRegen() {
                return getStats().getMana_Regen();
            }

            @Override
            public int getTotalCrit() {
                return getStats().getCrit();
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
        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);

    }

}
