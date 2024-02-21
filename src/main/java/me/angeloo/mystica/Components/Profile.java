package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import org.bukkit.inventory.ItemStack;

public interface Profile {

    Boolean getIfDead();
    Boolean getIfInCombat();
    void setIfDead(Boolean ifDead);
    void setIfInCombat(Boolean ifInCombat);
    double getCurrentHealth();
    void setCurrentHealth(double currentHealth);
    double getCurrentMana();
    void setCurrentMana(double currentMana);

    void setLevelStats(int level, String subclass);

    Stats getStats();
    Bal getBal();
    StatsFromGear getGearStats();
    void setStats(Stats stats);
    void setGearStats(StatsFromGear statsFromGear);


    int getTotalHealth();
    int getTotalMana();
    int getTotalAttack();
    int getTotalMagic();
    int getTotalDefense();
    int getTotalMagicDefense();
    int getTotalRegen();
    int getTotalManaRegen();
    int getTotalCrit();

    String getPlayerClass();
    void setPlayerClass(String playerClass);
    String getPlayerSubclass();
    void setPlayerSubclass(String playerSubclass);

    ItemStack[] getSavedInv();
    void setSavedInv(ItemStack[] inv);
    void removeSavedInv();

    PlayerBag getPlayerBag();
    PlayerEquipment getPlayerEquipment();

    PlayerBossLevel getPlayerBossLevel();



    Skill_Level getSkillLevels();
    EquipSkills getEquipSkills();

    Boolean getIsPassive();
    Boolean getIsMovable();
    void setIsMovable(Boolean which);
    Boolean getImmortality();
    void setImmortality(boolean immortality);
    Boolean getIfObject();
    Yield getYield();


    Milestones getMilestones();
}
