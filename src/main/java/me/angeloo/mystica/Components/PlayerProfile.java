package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.Guis.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerProfile implements Profile{

    private Boolean ifDead;
    private Boolean ifInCombat;

    private double currentHealth;

    private Stats stats;
    private StatsFromGear statsFromGear;

    private PlayerClass playerClass;
    private SubClass playerSubclass;

    private final MysticaBagCollection mysticaBagCollection;
    private final PlayerEquipment playerEquipment;

    private final Skill_Level skillLevel;
    private final EquipSkills equipSkills;

    private final PlayerBossLevel playerBossLevel;

    private final Map<String, QuestProgress> questProgressMap = new HashMap<>();

    public PlayerProfile(
            Boolean ifDead,
            Boolean ifInCombat,

            double currentHealth,

            Stats stats,
            StatsFromGear statsFromGear,

            PlayerClass playerClass,
            SubClass playerSubclass,

            MysticaBagCollection mysticaBagCollection,
            PlayerEquipment playerEquipment,

            Skill_Level skillLevel,
            EquipSkills equipSkills,

            PlayerBossLevel playerBossLevel

   ) {

        this.ifDead = ifDead;
        this.ifInCombat = ifInCombat;

        this.currentHealth = currentHealth;

        this.stats = stats;
        this.statsFromGear = statsFromGear;


        this.playerClass = playerClass;
        this.playerSubclass = playerSubclass;


        this.mysticaBagCollection = mysticaBagCollection;
        this.playerEquipment = playerEquipment;

        this.skillLevel = skillLevel;
        this.equipSkills = equipSkills;

        this.playerBossLevel = playerBossLevel;

        //this.questProgressMap = questProgressMap;

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
        return statsFromGear;
    }

    @Override
    public void setStats(Stats stats) {
        this.stats = stats;
    }


    @Override
    public void setGearStats(StatsFromGear gearStats) {
        this.statsFromGear = gearStats;
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
    public MysticaBagCollection getMysticaBagCollection(){
        return mysticaBagCollection;
    }
    @Override
    public PlayerEquipment getPlayerEquipment(){return playerEquipment;}
    @Override
    public Skill_Level getSkillLevels(){return skillLevel;}
    @Override
    public EquipSkills getEquipSkills(){return equipSkills;}
    @Override
    public PlayerBossLevel getPlayerBossLevel(){return playerBossLevel;}



    @Override
    public Map<String, QuestProgress> getQuestProgressMap() {
        return questProgressMap;
    }

    public void addQuestProgress(QuestProgress progress) {
        questProgressMap.put(progress.getQuest().getId(), progress);
    }



    public void removeQuestProgress(String questId) {
        questProgressMap.remove(questId);
    }

    public void lazyInnitQuestProgress(Map<String, QuestProgress> newMap){

        this.questProgressMap.putAll(newMap);

    }


    public int getTotalHealth(){
        return stats.getHealth() + statsFromGear.getHealth();
    }
    public int getTotalAttack(){
        return stats.getAttack() + statsFromGear.getAttack();
    }
    public int getTotalDefense(){
        return stats.getDefense() + statsFromGear.getDefense();
    }
    public int getTotalMagicDefense(){
        return stats.getMagic_Defense() + statsFromGear.getMagic_Defense();
    }
    public int getTotalCrit(){
        return stats.getCrit() + statsFromGear.getCrit();
    }

}
