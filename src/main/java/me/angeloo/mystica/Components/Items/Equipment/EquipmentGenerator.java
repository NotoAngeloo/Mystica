package me.angeloo.mystica.Components.Items.Equipment;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Utility.EquipmentSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EquipmentGenerator {

    private static final Random RANDOM = new Random();

    public MysticaEquipment generate(
            EquipmentSlot slot,
            PlayerClass playerClass,
            int level,
            int tier
    ){

        List<StatRoll> statRolls = generateStatRolls(level, tier);

        List<SkillRoll> skillRolls = generateSkillRolls(tier);

        return new MysticaEquipment(slot, playerClass, level, tier, statRolls, skillRolls);
    }

    private List<StatRoll> generateStatRolls(int level, int tier){

        if(tier<2){
            return Collections.emptyList();
        }

        List<StatType> available = new ArrayList<>(List.of(
                StatType.ATTACK,
                StatType.HEALTH,
                StatType.DEFENSE,
                StatType.MAGIC_DEFENSE,
                StatType.CRIT
        ));

        Collections.shuffle(available);

        List<StatRoll> rolls = new ArrayList<>();

        StatType highStat = available.get(0);
        StatType lowStat = available.get(1);

        rolls.add(new StatRoll(highStat, getHighStatAmount(highStat, level)));

        rolls.add(new StatRoll(lowStat, getLowStatAmount(lowStat, level)));

        return rolls;
    }

    private List<SkillRoll> generateSkillRolls(
            int tier
    ) {

        if(tier < 3) {
            return Collections.emptyList();
        }

        List<SkillRoll> rolls = new ArrayList<>();

        for(int i = 0; i < 2; i++) {

            int skillId =
                    RANDOM.nextInt(8) + 1;

            int amount =
                    RANDOM.nextInt(5) + 1;

            rolls.add(new SkillRoll(
                    skillId,
                    amount
            ));
        }

        return rolls;
    }

    private int getHighStatAmount(StatType statType, int level){
        switch (statType) {
            case ATTACK, DEFENSE, MAGIC_DEFENSE -> {
                return getHighAttackOrDefense(level);
            }
            case HEALTH -> {
                return getHighHealth(level);
            }
            case CRIT -> {
                return getHighCrit();
            }
        }
        return 0;
    }
    private int getLowStatAmount(StatType statType, int level){
        switch (statType) {
            case ATTACK, DEFENSE, MAGIC_DEFENSE -> {
                return getLowAttackOrDefense(level);
            }
            case HEALTH -> {
                return getLowHealth(level);
            }
            case CRIT -> {
                return getLowCrit();
            }
        }
        return 0;
    }

    private int getLowAttackOrDefense(int level){
        level--;
        return 10 * (1 + level);
    }
    private int getHighAttackOrDefense(int level){
        level--;
        return 20 * (1 + level);
    }
    private int getLowHealth(int level){
        level--;
        return 5 * (1 + level);
    }
    private int getHighHealth(int level){
        level--;
        return 10 * (1 + level);
    }


    private int getLowCrit(){
        return 5;
    }
    private int getHighCrit(){
        return 10;
    }



}
