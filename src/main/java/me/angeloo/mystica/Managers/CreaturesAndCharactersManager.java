package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import me.angeloo.mystica.Components.Creatures.*;
import me.angeloo.mystica.Components.Guis.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CreaturesAndCharactersManager {

    private final Mystica main;

    private final ProfileManager profileManager;

    private final TheLindwyrm theLindwyrm;
    private final CorruptHeart corruptHeart;
    private final WeberBoss weberBoss;
    private final NewPlayerNpc newPlayerNpc;
    private final ArchbishopNpc archbishopNpc;
    private final HansNpc hansNpc;
    private final HoLeeNpc hoLeeNpc;
    private final CaptainNpc captainNpc;
    private final HoLeeBoss hoLeeBoss;
    private final CoersicaBoss coersicaBoss;

    private final SammingSins sammingSins;
    private final Luna luna;
    private final Wings wings;
    private final Darwin darwin;
    private final Slippy slippy;

    public CreaturesAndCharactersManager(Mystica main, ProfileManager profileManager, MysticaPartyManager mysticaPartyManager){
        this.main = main;
        this.profileManager = profileManager;


        theLindwyrm = new TheLindwyrm(main, profileManager, mysticaPartyManager);
        corruptHeart = new CorruptHeart(main, profileManager, mysticaPartyManager);
        weberBoss = new WeberBoss(main, profileManager, mysticaPartyManager);
        newPlayerNpc = new NewPlayerNpc(main, profileManager);
        archbishopNpc = new ArchbishopNpc(main, profileManager);
        hansNpc = new HansNpc(main, profileManager);
        hoLeeNpc = new HoLeeNpc(main, profileManager);
        captainNpc = new CaptainNpc(main, profileManager);
        hoLeeBoss = new HoLeeBoss(main, profileManager, mysticaPartyManager);
        coersicaBoss = new CoersicaBoss(main, profileManager, mysticaPartyManager);
        sammingSins = new SammingSins(main, profileManager, mysticaPartyManager);
        luna = new Luna(main, profileManager, mysticaPartyManager);
        wings = new Wings(main, profileManager, mysticaPartyManager);
        darwin = new Darwin(main, profileManager, mysticaPartyManager);
        slippy = new Slippy(main, profileManager, mysticaPartyManager);
    }

    public void spawnAllNpcs() throws InvalidMobTypeException {
        archbishopNpc.spawn();
        hansNpc.spawn();
        //hoLeeNpc.spawn();
        //captainNpc.spawn();
    }

    public void cancelSpawnTasks(){
        archbishopNpc.getSpawnTask().cancel();
    }


    public void makeNpcProfile(String name, UUID uuid){

        switch (name) {
            case "Lindwyrm" -> {
                theLindwyrm.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                profileManager.setBossIcon(uuid, name);
            }
            case "WeberBoss" -> {
                weberBoss.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                profileManager.setBossIcon(uuid, name);
            }
            case "CoersicaBoss" -> {
                coersicaBoss.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                profileManager.setBossIcon(uuid, name);
            }
            case "Coersica_Dimension_Rift" -> {
                coersicaBoss.makeSpawnablePortalProfile(uuid);
            }
            case "Coersica_Shadow_Elemental" -> {
                coersicaBoss.makeSpawnableProfile(uuid);
            }
            case "HoLeeBoss" -> {
                hoLeeBoss.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                profileManager.setBossIcon(uuid, name);
            }
            case "HoLeePikeman" -> {
                hoLeeBoss.makeSpawnableProfile(uuid);
            }
            case "CorruptHeart" -> {
                corruptHeart.makeProfile(uuid);
                profileManager.setBossHome(uuid);
                profileManager.setBossIcon(uuid, name);
            }
            case "SafeZone", "TankZone", "RangedZone", "MeleeZone", "Nothing", "DemonPortal", "HeartTendril", "Dimension_Rift", "ChaosSeed", "ShadowSeed", "Coersica_Shadow_Grip", "LindwyrmRock", "Corpse" -> {
                makeImmortalObjectProfile(uuid);
            }
            case "FastTravelNpc", "HoLeeNpc", "CaptainNpc", "HansNpc", "NewPlayerNpc", "ClassTutorial", "LunaNpc", "ArchbishopNpc" -> {
                makeDefaultNonCombatantProfile(uuid);
            }
            case "Slippy" -> {
                slippy.makeProfile(uuid);
            }
            case "Darwin" -> {
                darwin.makeProfile(uuid);
            }
            case "SammingSins" -> {
                sammingSins.makeProfile(uuid);
            }
            case "Luna" -> {
                luna.makeProfile(uuid);
            }
            case "Wings" -> {
                wings.makeProfile(uuid);
            }
            default -> {
                profileManager.createNewDefaultNonPlayerProfile(uuid);
            }
        }
    }

    private void makeDefaultNonCombatantProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,10,1,0);
        Boolean isMovable = false;
        Boolean immortal = true;
        Boolean object = false;
        Boolean passive = true;
        Yield yield = new Yield(0.0f, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(false, 10, stats, isMovable, immortal, passive, object, yield) {


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
            public PlayerClass getPlayerClass() {
                return null;
            }

            @Override
            public void setPlayerClass(PlayerClass playerClass) {

            }

            @Override
            public SubClass getPlayerSubclass() {
                return null;
            }

            @Override
            public void setPlayerSubclass(SubClass playerSubclass) {

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
            public void getVoidsOnDeath(Set<Player> players) {

            }

            @Override
            public Map<String, QuestProgress> getQuestProgressMap() {
                return null;
            }

            @Override
            public void addQuestProgress(QuestProgress progress) {

            }

            @Override
            public void removeQuestProgress(String questId) {

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

        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }

    private void makeImmortalObjectProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,10,10,0);
        boolean isDead = false;
        Boolean isMovable = false;
        Boolean immortal = true;
        Boolean object = true;
        Boolean passive = true;
        Yield yield = new Yield(0.0f, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(isDead, 10, stats, isMovable, immortal, passive, object, yield) {


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
            public PlayerClass getPlayerClass() {
                return null;
            }

            @Override
            public void setPlayerClass(PlayerClass playerClass) {

            }

            @Override
            public SubClass getPlayerSubclass() {
                return null;
            }

            @Override
            public void setPlayerSubclass(SubClass playerSubclass) {

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
            public void getVoidsOnDeath(Set<Player> players) {

            }

            @Override
            public Map<String, QuestProgress> getQuestProgressMap() {
                return null;
            }

            @Override
            public void addQuestProgress(QuestProgress progress) {

            }

            @Override
            public void removeQuestProgress(String questId) {

            }


            @Override
            public MysticaBagCollection getMysticaBagCollection() {
                return null;
            }
        };
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }


}
