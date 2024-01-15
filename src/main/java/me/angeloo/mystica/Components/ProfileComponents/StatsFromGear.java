package me.angeloo.mystica.Components.ProfileComponents;

public class StatsFromGear {

    private int Attack;
    private int Magic;
    private int Health;
    private int Mana;
    private int Regen;
    private int Mana_Regen;
    private int Defense;
    private int Magic_Defense;
    private int Crit;


    public StatsFromGear(int attack, int magic, int health, int mana, int regen, int mana_regen, int defense, int magic_defense, int crit) {
        Attack = attack;
        Magic = magic;
        Health = health;
        Mana = mana;
        Regen = regen;
        Mana_Regen = mana_regen;
        Defense = defense;
        Magic_Defense = magic_defense;
        Crit = crit;
    }
    public int getAttack() {
        return Attack;
    }

    public int getMagic() {
        return Magic;
    }

    public int getHealth() {
        return Health;
    }

    public int getMana() {
        return Mana;
    }

    public int getRegen() {
        return Regen;
    }

    public int getMana_Regen() {
        return Mana_Regen;
    }

    public int getDefense() {
        return Defense;
    }

    public int getMagic_Defense() {
        return Magic_Defense;
    }

    public int getCrit() {
        return Crit;
    }


    public void setAllGearStats(int attack, int magic, int health, int mana, int regen, int mana_regen, int defense, int magic_defense, int crit){
        Attack = attack;
        Magic = magic;
        Health = health;
        Mana = mana;
        Regen = regen;
        Mana_Regen = mana_regen;
        Defense = defense;
        Magic_Defense = magic_defense;
        Crit = crit;
    }

}
