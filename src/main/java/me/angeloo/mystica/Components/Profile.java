package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

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
    int getTotalDefense();
    int getTotalMagicDefense();
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

    Boolean getImmortality();

    Boolean getIfObject();
    Yield getYield();

    Milestones getMilestones();

    void getVoidsOnDeath(Set<Player> players);
}
