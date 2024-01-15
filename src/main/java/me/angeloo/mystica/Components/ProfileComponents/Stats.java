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


    public void setLevelStats(int level, String subclass){

        int attack = 50;
        int magic = 50;
        int health = 100;
        int mana = 100;
        int regen = 1;
        int mana_regen = 1;
        int defence = 50;
        int magic_defence = 50;
        int crit = 1;

        switch (subclass.toLowerCase()){

            case "pyromancer":
            case "chaos":
            case "arcane master":{
                health+=(level*15);
                regen+=(level*1.15);
                mana_regen+=(level*1.15);
                magic+=(level*3);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);
                crit+=10;
                break;
            }
            case "conjurer":
            case "shepard": {
                health+=(level*30);
                regen+=(level*1.3);
                mana_regen+=(level*1.3);
                magic+=(level*2);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);
                break;
            }
            case "scout":
            case "executioner":
            case "dawn":
            case "duelist":{
                health+=(level*15);
                regen+=(level*1.15);
                mana_regen+=(level*1.15);
                attack+=(level*3);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);
                crit+=10;
                break;
            }
            case "animal tamer":
            case "divine":
            case "alchemist":{
                health+=(level*30);
                attack+=(level*2);
                mana+=(level*100);
                defence+=(level);
                magic_defence+=(level);
                break;
            }
            case "blood":{
                health+=(level*40);
                regen+=(level*1.4);
                mana_regen=30;
                attack+=(level);
                mana=100;
                defence+=(level*2);
                magic_defence+=(level*2);
                break;
            }
            case "doom":{
                health+=(level*30);
                regen+=(level*1.3);
                mana_regen=30;
                attack+=(level*3);
                mana=100;
                defence+=(level);
                magic_defence+=(level);
                crit+=10;
                break;
            }
            case "templar":
            case "gladiator":{
                health+=(level*30);
                regen+=(level*1.3);
                mana_regen+=(level*1.3);
                attack+=(level);
                mana+=(level*200);
                defence+=(level*2);
                magic_defence+=(level*2);
                break;
            }
        }

        Attack = attack;
        Magic = magic;
        Health = health;
        Mana = mana;
        Regen = regen;
        Mana_Regen = mana_regen;
        Defense = defence;
        Magic_Defense = magic_defence;
        Crit = crit;
    }


}
