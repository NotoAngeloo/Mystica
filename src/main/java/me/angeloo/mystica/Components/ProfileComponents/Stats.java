package me.angeloo.mystica.Components.ProfileComponents;

public class Stats {

    private int Level;
    private int Attack;
    private int Health;
    private int Mana;
    private int Defense;
    private int Magic_Defense;
    private int Crit;


    public Stats(int level, int attack, int health, int mana, int defense, int magic_defense, int crit) {
        Level = level;
        Attack = attack;
        Health = health;
        Mana = mana;
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

    public int getMana() {
        return Mana;
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


    public void setLevelStats(int level, String playerClass, String subclass){

        //base
        int attack = 50;
        int health = 100;
        int mana = 500;
        int defence = 50;
        int magic_defence = 50;
        int crit = 1;

        switch (playerClass.toLowerCase()){
            case "assassin":{
                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);

                switch (subclass.toLowerCase()){
                    case "duelist":{
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "alchemist":{
                        health+=(level*15);
                        break;
                    }
                }


                break;
            }
            case "elementalist":{

                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);

                switch (subclass.toLowerCase()){
                    case "pyromancer":{
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "conjurer":{
                        health+=(level*15);
                        break;
                    }
                }

                break;
            }
            case "mystic":{

                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);

                switch (subclass.toLowerCase()){
                    case "arcane master":
                    case "chaos":{
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "shepard":{
                        health+=(level*15);
                        break;
                    }
                }

                break;
            }
            case "paladin":{

                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);

                switch (subclass.toLowerCase()){
                    case "dawn": {
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "divine":{
                        health+=(level*15);
                        break;
                    }
                    case "templar":{
                        attack-=(level);
                        health+=(level*15);
                        defence+=(level);
                        magic_defence+=(level);
                        break;
                    }
                }

                break;
            }
            case "ranger":{
                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);

                switch (subclass.toLowerCase()){
                    case "scout":{
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "animal tamer":{
                        health+=(level*15);
                        break;
                    }
                }


                break;
            }
            case "shadow knight":{
                attack+=(level*2);
                health+=(level*15);
                defence+=(level);
                magic_defence+=(level);
                mana = 100;

                switch (subclass.toLowerCase()){
                    case "doom": {
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "blood":{
                        attack-=(level);
                        health+=(level*15);
                        defence+=(level);
                        magic_defence+=(level);
                        break;
                    }
                }

                break;
            }
            case "warrior":{

                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);

                switch (subclass.toLowerCase()){
                    case "executioner": {
                        attack+=level;
                        crit+=10;
                        break;
                    }
                    case "gladiator":{
                        attack-=(level);
                        health+=(level*15);
                        defence+=(level);
                        magic_defence+=(level);
                        break;
                    }
                }

                break;
            }
            case "none":{
                attack+=(level*2);
                health+=(level*15);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);
            }
        }


        Attack = attack;
        Health = health;
        Mana = mana;
        Defense = defence;
        Magic_Defense = magic_defence;
        Crit = crit;
    }



}
