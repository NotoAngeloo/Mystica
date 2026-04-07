package me.angeloo.mystica.Components.ProfileComponents;

import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;

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



    public void setLevelStats(int level, PlayerClass playerClass, SubClass subclass){

        //base
        int attack = 50;
        int health = 100;
        int defence = 50;
        int magic_defence = 50;
        int crit = 1;

        switch (playerClass) {
            case Assassin -> {
                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Duelist -> {
                        attack += level;
                        crit += 10;
                    }
                    case Alchemist -> {
                        health += (level * 15);
                    }
                }


            }
            case Elementalist -> {

                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Pyromancer -> {
                        attack += level;
                        crit += 10;
                    }
                    case Conjurer -> {
                        health += (level * 15);
                    }
                }

            }
            case Mystic -> {

                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Arcane, Chaos -> {
                        attack += level;
                        crit += 10;
                    }
                    case Shepard -> {
                        health += (level * 15);
                    }
                }

            }
            case Paladin -> {

                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Dawn -> {
                        attack += level;
                        crit += 10;
                    }
                    case Divine -> {
                        health += (level * 15);
                    }
                    case Templar -> {
                        attack -= (level);
                        health += (level * 15);
                        defence += (level);
                        magic_defence += (level);
                    }
                }

            }
            case Ranger -> {
                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Scout -> {
                        attack += level;
                        crit += 10;
                    }
                    case Tamer -> {
                        health += (level * 15);
                    }
                }


            }
            case Shadow_Knight -> {
                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Doom -> {
                        attack += level;
                        crit += 10;
                    }
                    case Blood -> {
                        attack -= (level);
                        health += (level * 15);
                        defence += (level);
                        magic_defence += (level);
                    }
                }

            }
            case Warrior -> {

                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);

                switch (subclass) {
                    case Executioner -> {
                        attack += level;
                        crit += 10;
                    }
                    case Gladiator -> {
                        attack -= (level);
                        health += (level * 15);
                        defence += (level);
                        magic_defence += (level);
                    }
                }

            }
            case NONE -> {
                attack += (level * 2);
                health += (level * 15);
                defence += (level);
                magic_defence += (level);
            }
        }


        Attack = attack;
        Health = health;
        Defense = defence;
        Magic_Defense = magic_defence;
        Crit = crit;
    }



}
