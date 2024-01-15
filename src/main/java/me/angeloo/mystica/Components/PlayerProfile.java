package me.angeloo.mystica.Components;

import me.angeloo.mystica.Components.ProfileComponents.*;
import org.bukkit.inventory.ItemStack;

public abstract class PlayerProfile implements Profile{

    private Boolean ifDead;
    private Boolean ifInCombat;

    private double currentHealth;
    private double currentMana;

    private Stats stats;
    private StatsFromGear statsFromGear;

    private String  playerClass;
    private String playerSubclass;

    private ItemStack[] savedInv;

    private final PlayerBag playerBag;
    private final PlayerEquipment playerEquipment;

    private final Skill_Level skillLevel;
    private final EquipSkills equipSkills;

    private final PlayerBossLevel playerBossLevel;

    public PlayerProfile(
            Boolean ifDead,
            Boolean ifInCombat,

            double currentHealth,
            double currentMana,

            Stats stats,
            StatsFromGear statsFromGear,

            Bal bal,

            String playerClass,
            String playerSubclass,
            ItemStack[] savedInv,

            PlayerBag playerbag,
            PlayerEquipment playerEquipment,

            Skill_Level skillLevel,
            EquipSkills equipSkills,

            PlayerBossLevel playerBossLevel

   ) {

        this.ifDead = ifDead;
        this.ifInCombat = ifInCombat;

        this.currentHealth = currentHealth;
        this.currentMana = currentMana;

        this.stats = stats;
        this.statsFromGear = statsFromGear;


        this.playerClass = playerClass;
        this.playerSubclass = playerSubclass;

        this.savedInv = savedInv;

        this.playerBag = playerbag;
        this.playerEquipment = playerEquipment;

        this.skillLevel = skillLevel;
        this.equipSkills = equipSkills;

        this.playerBossLevel = playerBossLevel;
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
    public double getCurrentMana() {
        return currentMana;
    }
    @Override
    public void setCurrentMana(double currentMana) {
        this.currentMana = currentMana;
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
    public String getPlayerClass(){
        return playerClass;
    }
    @Override
    public void setPlayerClass(String playerClass){
        this.playerClass = playerClass;
    }
    @Override
    public String getPlayerSubclass(){return playerSubclass;}
    @Override
    public void setPlayerSubclass(String subclass){
        this.playerSubclass = subclass;
    }
    @Override
    public ItemStack[] getSavedInv(){return savedInv;}
    @Override
    public void setSavedInv(ItemStack[] inv){
        this.savedInv = inv;
    }
    @Override
    public void removeSavedInv(){
        savedInv = new ItemStack[41];
    }
    @Override
    public PlayerBag getPlayerBag(){return playerBag;}
    @Override
    public PlayerEquipment getPlayerEquipment(){return  playerEquipment;}
    @Override
    public Skill_Level getSkillLevels(){return skillLevel;}
    @Override
    public EquipSkills getEquipSkills(){return equipSkills;}
    @Override
    public PlayerBossLevel getPlayerBossLevel(){return playerBossLevel;}

    public int getTotalHealth(){
        return stats.getHealth() + statsFromGear.getHealth();
    }
    public int getTotalAttack(){
        return stats.getAttack() + statsFromGear.getAttack();
    }
    public int getTotalMagic(){
        return stats.getMagic() + statsFromGear.getMagic();
    }
    public int getTotalMana(){
        return stats.getMana() + statsFromGear.getMana();
    }
    public int getTotalRegen(){
        return stats.getRegen() + statsFromGear.getRegen();
    }
    public int getTotalManaRegen(){
        return stats.getMana_Regen() + statsFromGear.getMana_Regen();
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
