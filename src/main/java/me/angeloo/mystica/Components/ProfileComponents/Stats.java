package me.angeloo.mystica.Components.ProfileComponents;

public class Stats {

    private int Level;
    private int Attack;
    private int Magic;
    private int Health;
    private int Mana;
    private int Regen;
    private int Mana_Regen;
    private int Defense;
    private int Magic_Defense;
    private int Crit;


    public Stats(int level, int attack, int magic, int health, int mana, int regen, int mana_regen, int defense, int magic_defense, int crit) {
        Level = level;
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
    public int getLevel(){return Level;}

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





    public void setLevel(int level){Level = level;}

    public void setAttack(int attack) {
        Attack = attack;
    }

    public void setMagic(int magic) {
        Magic = magic;
    }

    public void setHealth(int health) {
        Health = health;
    }

    public void setMana(int mana) {
        Mana = mana;
    }

    public void setRegen(int regen) {
        Regen = regen;
    }

    public void setMana_Regen(int mana_regen) {
        Mana_Regen = mana_regen;
    }

    public void setDefense(int defense) {
        Defense = defense;
    }

    public void setMagic_Defense(int magic_defense) {
        Magic_Defense = magic_defense;
    }

    public void setCrit(int crit) {
        Crit = crit;
    }


}
