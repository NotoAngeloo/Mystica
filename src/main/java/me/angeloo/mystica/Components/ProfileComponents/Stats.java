package me.angeloo.mystica.Components.ProfileComponents;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;

public class Stats {

    private int Level;
    private int Attack;
    private int Health;
    private int Defense;
    private int Magic_Defense;
    private int Crit;


    public Stats(int level, int attack, int health, int defense, int magic_defense, int crit) {
        Level = level;
        Attack = attack;
        Health = health;
        Defense = defense;
        Magic_Defense = magic_defense;
        Crit = crit;

    }
    public int getLevel(){return Level;}

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

    public void setLevel(int level){Level = level;}


}
