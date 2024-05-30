package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Creatures.*;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CreaturesAndCharactersManager {

    private final ProfileManager profileManager;

    private final TheLindwyrm theLindwyrm;
    private final CorruptHeart corruptHeart;
    private final BetaTester betaTester;
    private final Dummy dummy;
    private final MadDummy madDummy;
    private final NewPlayerNpc newPlayerNpc;
    private final ArchbishopNpc archbishopNpc;
    private final HansNpc hansNpc;
    private final HoLeeNpc hoLeeNpc;
    private final CaptainNpc captainNpc;
    private final HoLeeBoss hoLeeBoss;

    private final SammingSins sammingSins;

    public CreaturesAndCharactersManager(Mystica main){
        profileManager = main.getProfileManager();
        theLindwyrm = new TheLindwyrm(main);
        corruptHeart = new CorruptHeart(main);
        betaTester = new BetaTester(main);
        dummy = new Dummy(main);
        madDummy = new MadDummy(main);
        newPlayerNpc = new NewPlayerNpc(main);
        archbishopNpc = new ArchbishopNpc(main);
        hansNpc = new HansNpc(main);
        hoLeeNpc = new HoLeeNpc(main);
        captainNpc = new CaptainNpc(main);
        hoLeeBoss = new HoLeeBoss(main);
        sammingSins = new SammingSins(main);
    }

    public void spawnAllNpcs() throws InvalidMobTypeException {
        newPlayerNpc.spawn();
        dummy.spawn();
        archbishopNpc.spawn();
        hansNpc.spawn();
        hoLeeNpc.spawn();
        captainNpc.spawn();
    }

    public void spawnCompanions(Player player) {

        Entity tank;

        try{
            tank = MythicBukkit.inst().getAPIHelper().spawnMythicMob("SammingSins", player.getLocation());
        }catch (InvalidMobTypeException e) {
            return;
        }


        profileManager.getAnyProfile((LivingEntity) tank);

    }

    public void makeNpcProfile(String name, UUID uuid){

        switch (name){
            case "Lindwyrm":{
                theLindwyrm.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                break;
            }
            case "HoLeeBoss":{
                hoLeeBoss.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                break;
            }
            case "HoLeePikeman":{
                hoLeeBoss.makeSpawnableProfile(uuid);
                break;
            }
            case "CorruptHeart":{
                corruptHeart.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                break;
            }
            case "DemonPortal":
            case "HeartTendril":
            case "LindwyrmRock":{
                makeImmortalObjectProfile(uuid);
                break;
            }
            case "BetaTester":{
                betaTester.makeProfile(uuid);
                break;
            }
            case "Dummy":{
                dummy.makeProfile(uuid);
                break;
            }
            case "MadDummy":{
                madDummy.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                break;
            }
            case "FastTravelNpc":
            case "HoLeeNpc":
            case "CaptainNpc":
            case "HansNpc":
            case "NewPlayerNpc":
            case "ClassTutorial":
            case "ArchbishopNpc":{
                makeDefaultNonCombatantProfile(uuid);
                break;
            }
            case "SammingSins":{
                sammingSins.makeProfile(uuid);
                break;
            }
            default:{
                profileManager.createNewDefaultNonPlayerProfile(uuid);
                break;
            }
        }
    }

    private void makeDefaultNonCombatantProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,10,1,0);
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

                Entity entity = Bukkit.getEntity(uuid);

                if(entity != null){
                    return entity.isDead();
                }

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
            public int getTotalDefense() {
                return 0;
            }

            @Override
            public int getTotalMagicDefense() {
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
            public Boolean fakePlayer() {
                return false;
            }

            @Override
            public Milestones getMilestones() {
                return null;
            }

            @Override
            public void getVoidsOnDeath(Set<Player> players) {

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
        Stats stats = new Stats(1,1,1,1,10,10,0);
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

                Entity entity = Bukkit.getEntity(uuid);

                if(entity != null){
                    return entity.isDead();
                }

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
            public int getTotalDefense() {
                return 0;
            }

            @Override
            public int getTotalMagicDefense() {
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
            public Boolean fakePlayer() {
                return false;
            }

            @Override
            public Milestones getMilestones() {
                return null;
            }

            @Override
            public void getVoidsOnDeath(Set<Player> players) {

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
