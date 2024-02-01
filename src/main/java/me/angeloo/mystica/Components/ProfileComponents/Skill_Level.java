package me.angeloo.mystica.Components.ProfileComponents;

public class Skill_Level {

    private int Skill_1_Level;
    private int Skill_2_Level;
    private int Skill_3_Level;
    private int Skill_4_Level;
    private int Skill_5_Level;
    private int Skill_6_Level;
    private int Skill_7_Level;
    private int Skill_8_Level;

    private int Skill_1_Level_Bonus;
    private int Skill_2_Level_Bonus;
    private int Skill_3_Level_Bonus;
    private int Skill_4_Level_Bonus;
    private int Skill_5_Level_Bonus;
    private int Skill_6_Level_Bonus;
    private int Skill_7_Level_Bonus;
    private int Skill_8_Level_Bonus;

    public Skill_Level(int skill_1_Level, int skill_2_Level, int skill_3_Level, int skill_4_Level, int skill_5_Level, int skill_6_Level, int skill_7_Level, int skill_8_Level,
                       int skill_1_Level_Bonus, int skill_2_Level_Bonus, int skill_3_Level_Bonus, int skill_4_Level_Bonus,
                       int skill_5_Level_Bonus, int skill_6_Level_Bonus, int skill_7_Level_Bonus, int skill_8_Level_Bonus){
        Skill_1_Level = skill_1_Level;
        Skill_2_Level = skill_2_Level;
        Skill_3_Level = skill_3_Level;
        Skill_4_Level = skill_4_Level;
        Skill_5_Level = skill_5_Level;
        Skill_6_Level = skill_6_Level;
        Skill_7_Level = skill_7_Level;
        Skill_8_Level = skill_8_Level;

        Skill_1_Level_Bonus = skill_1_Level_Bonus;
        Skill_2_Level_Bonus = skill_2_Level_Bonus;
        Skill_3_Level_Bonus = skill_3_Level_Bonus;
        Skill_4_Level_Bonus = skill_4_Level_Bonus;
        Skill_5_Level_Bonus = skill_5_Level_Bonus;
        Skill_6_Level_Bonus = skill_6_Level_Bonus;
        Skill_7_Level_Bonus = skill_7_Level_Bonus;
        Skill_8_Level_Bonus = skill_8_Level_Bonus;
    }

    public int getSkill_1_Level() {
        return Skill_1_Level;
    }

    public void setSkill_1_Level(int skill_1_Level) {
        Skill_1_Level = skill_1_Level;
    }

    public int getSkill_2_Level() {
        return Skill_2_Level;
    }

    public void setSkill_2_Level(int skill_2_Level) {
        Skill_2_Level = skill_2_Level;
    }

    public int getSkill_3_Level() {
        return Skill_3_Level;
    }

    public void setSkill_3_Level(int skill_3_Level) {
        Skill_3_Level = skill_3_Level;
    }

    public int getSkill_4_Level() {
        return Skill_4_Level;
    }

    public void setSkill_4_Level(int skill_4_Level) {
        Skill_4_Level = skill_4_Level;
    }

    public int getSkill_5_Level() {
        return Skill_5_Level;
    }

    public void setSkill_5_Level(int skill_5_Level) {
        Skill_5_Level = skill_5_Level;
    }

    public int getSkill_6_Level() {
        return Skill_6_Level;
    }

    public void setSkill_6_Level(int skill_6_Level) {
        Skill_6_Level = skill_6_Level;
    }

    public int getSkill_7_Level() {
        return Skill_7_Level;
    }

    public void setSkill_7_Level(int skill_7_Level) {
        Skill_7_Level = skill_7_Level;
    }

    public int getSkill_8_Level() {
        return Skill_8_Level;
    }

    public void setSkill_8_Level(int skill_8_Level) {
        Skill_8_Level = skill_8_Level;
    }

    public int getSkill_1_Level_Bonus() {
        return Skill_1_Level_Bonus;
    }
    public int getSkill_2_Level_Bonus() {
        return Skill_2_Level_Bonus;
    }
    public int getSkill_3_Level_Bonus() {
        return Skill_3_Level_Bonus;
    }
    public int getSkill_4_Level_Bonus() {
        return Skill_4_Level_Bonus;
    }
    public int getSkill_5_Level_Bonus() {
        return Skill_5_Level_Bonus;
    }
    public int getSkill_6_Level_Bonus() {
        return Skill_6_Level_Bonus;
    }
    public int getSkill_7_Level_Bonus() {
        return Skill_7_Level_Bonus;
    }
    public int getSkill_8_Level_Bonus() {
        return Skill_8_Level_Bonus;
    }

    public void setAllSkillLevelBonus(int skill1, int skill2, int skill3, int skill4, int skill5, int skill6, int skill7, int skill8){
        Skill_1_Level_Bonus = skill1;
        Skill_2_Level_Bonus = skill2;
        Skill_3_Level_Bonus = skill3;
        Skill_4_Level_Bonus = skill4;
        Skill_5_Level_Bonus = skill5;
        Skill_6_Level_Bonus = skill6;
        Skill_7_Level_Bonus = skill7;
        Skill_8_Level_Bonus = skill8;
    }
}
