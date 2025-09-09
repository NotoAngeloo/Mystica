package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.Inventories.Storage.MysticaBagCollection;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public interface Profile {

    Boolean getIfDead();
    Boolean getIfInCombat();
    void setIfDead(Boolean ifDead);
    void setIfInCombat(Boolean ifInCombat);
    double getCurrentHealth();
    void setCurrentHealth(double currentHealth);

    void setLevelStats(int level, String subclass);

    Stats getStats();
    StatsFromGear getGearStats();
    void setStats(Stats stats);
    void setGearStats(StatsFromGear statsFromGear);


    int getTotalHealth();
    int getTotalAttack();
    int getTotalDefense();
    int getTotalMagicDefense();
    int getTotalCrit();

    PlayerClass getPlayerClass();
    void setPlayerClass(PlayerClass playerClass);
    SubClass getPlayerSubclass();
    void setPlayerSubclass(SubClass subclass);

    MysticaBagCollection getMysticaBagCollection();

    PlayerEquipment getPlayerEquipment();

    PlayerBossLevel getPlayerBossLevel();


    Skill_Level getSkillLevels();
    EquipSkills getEquipSkills();

    Boolean getIsPassive();
    Boolean getIsMovable();

    Boolean getImmortality();

    Boolean getIfObject();
    Yield getYield();

    Boolean fakePlayer();

    void getVoidsOnDeath(Set<Player> players);

    Map<String, QuestProgress> getQuestProgressMap();


    void addQuestProgress(QuestProgress progress);

    void removeQuestProgress(String questId);
}
