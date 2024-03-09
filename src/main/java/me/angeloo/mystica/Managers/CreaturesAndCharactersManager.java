package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Creatures.BetaTester;
import me.angeloo.mystica.Components.Creatures.Dummy;
import me.angeloo.mystica.Components.Creatures.TheLindwyrm;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CreaturesAndCharactersManager {

    private final ProfileManager profileManager;

    private final TheLindwyrm theLindwyrm;
    private final BetaTester betaTester;
    private final Dummy dummy;


    public CreaturesAndCharactersManager(Mystica main){
        profileManager = main.getProfileManager();
        theLindwyrm = new TheLindwyrm(main);
        betaTester = new BetaTester(main);
        dummy = new Dummy(main);
    }

    public void spawnAllNpcs(){

        try {
            MythicBukkit.inst().getAPIHelper().spawnMythicMob("NewPlayerNpc", new Location(Bukkit.getWorld("world"), 64, 99, -350, -130, 0));
            //616, 101, -88, 90, 0
            MythicBukkit.inst().getAPIHelper().spawnMythicMob("LindwyrmNpc", new Location(Bukkit.getWorld("world"), 617, 100, -88, 90, 0));
        }
        catch (InvalidMobTypeException e) {
            throw new RuntimeException(e);
        }
    }

    public void makeNpcProfile(String name, UUID uuid){

        switch (name){
            case "The Lindwyrm":{
                theLindwyrm.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                break;
            }
            case "Rock":{
                makeImmortalObjectProfile(uuid);
                break;
            }
            case "Beta Tester":{
                betaTester.makeProfile(uuid);
                break;
            }
            case "Dummy":{
                dummy.makeProfile(uuid);
                break;
            }
            case "Trenton Vocation":
            case "Dungeon Dan":{
                makeDefaultNonCombatantProfile(uuid);
                break;
            }
            default:{
                profileManager.createNewDefaultNonPlayerProfile(uuid);
                break;
            }
        }
    }

    private void makeDefaultNonCombatantProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,1,10,1,0,0,0);
        Boolean isMovable = false;
        Boolean immortal = true;
        Boolean object = false;
        Boolean passive = true;
        Yield yield = new Yield(0.0f, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(10, stats, isMovable, immortal, passive, object, yield) {
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
                return 0;
            }

            @Override
            public int getTotalMana() {
                return 0;
            }

            @Override
            public int getTotalAttack() {
                return 0;
            }

            @Override
            public int getTotalMagic() {
                return 0;
            }

            @Override
            public int getTotalDefense() {
                return 0;
            }

            @Override
            public int getTotalMagicDefense() {
                return 0;
            }

            @Override
            public int getTotalRegen() {
                return 0;
            }

            @Override
            public int getTotalManaRegen() {
                return 0;
            }

            @Override
            public int getTotalCrit() {
                return 0;
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
        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }

    private void makeImmortalObjectProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,1,10,1,0,0,0);
        Boolean isMovable = false;
        Boolean immortal = true;
        Boolean object = true;
        Boolean passive = true;
        Yield yield = new Yield(0.0f, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(10, stats, isMovable, immortal, passive, object, yield) {
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
                return 0;
            }

            @Override
            public int getTotalMana() {
                return 0;
            }

            @Override
            public int getTotalAttack() {
                return 0;
            }

            @Override
            public int getTotalMagic() {
                return 0;
            }

            @Override
            public int getTotalDefense() {
                return 0;
            }

            @Override
            public int getTotalMagicDefense() {
                return 0;
            }

            @Override
            public int getTotalRegen() {
                return 0;
            }

            @Override
            public int getTotalManaRegen() {
                return 0;
            }

            @Override
            public int getTotalCrit() {
                return 0;
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
            public ItemStack[] getSavedInv() {
                return new ItemStack[0];
            }

            @Override
            public void setSavedInv(ItemStack[] inv) {

            }

            @Override
            public void removeSavedInv() {

            }
        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }


}
