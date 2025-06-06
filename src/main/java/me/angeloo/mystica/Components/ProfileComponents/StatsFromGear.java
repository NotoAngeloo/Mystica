package me.angeloo.mystica.Components.ProfileComponents;

public class StatsFromGear {

    private int Attack;
    private int Health;
    private int Defense;
    private int Magic_Defense;
    private int Crit;


    public StatsFromGear(int attack, int health, int defense, int magic_defense, int crit) {
        Attack = attack;
        Health = health;
        Defense = defense;
        Magic_Defense = magic_defense;
        Crit = crit;
    }
    public int getAttack() {
        return Attack;
    }

    public int getHealth() {
        return Health;
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


    public void setAllGearStats(int attack, int health, int defense, int magic_defense, int crit){
        Attack = attack;
        Health = health;
        Defense = defense;
        Magic_Defense = magic_defense;
        Crit = crit;
    }

}
