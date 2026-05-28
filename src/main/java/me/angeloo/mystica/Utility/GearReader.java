package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Items.Equipment.*;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GearReader {

    private final ProfileManager profileManager;
    private final EquipmentStatCalculator statCalculator;

    public GearReader(Mystica main){
        profileManager = main.getProfileManager();
        statCalculator = new EquipmentStatCalculator();
    }

    public void setGearStats(Player player){

        int attack = 0;
        int health = 0;
        int defense = 0;
        int magic_defense = 0;
        int crit = 0;

        int skill_1 = 0;
        int skill_2 = 0;
        int skill_3 = 0;
        int skill_4 = 0;
        int skill_5 = 0;
        int skill_6 = 0;
        int skill_7 = 0;
        int skill_8 = 0;

        PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        for(MysticaEquipment item : equipment.getEquipment()){

            if(item == null){
                continue;
            }

            Map<Object, Integer> stats = new HashMap<>(getGearStats(item));

            attack+=stats.getOrDefault(StatType.ATTACK, 0);
            health+=stats.getOrDefault(StatType.HEALTH, 0);
            defense+=stats.getOrDefault(StatType.DEFENSE, 0);
            magic_defense+=stats.getOrDefault(StatType.MAGIC_DEFENSE, 0);
            crit+=stats.getOrDefault(StatType.CRIT, 0);

            skill_1+= stats.getOrDefault(1, 0);
            skill_2+= stats.getOrDefault(2, 0);
            skill_3+= stats.getOrDefault(3, 0);
            skill_4+= stats.getOrDefault(4, 0);
            skill_5+= stats.getOrDefault(5, 0);
            skill_6+= stats.getOrDefault(6, 0);
            skill_7+= stats.getOrDefault(7, 0);
            skill_8+= stats.getOrDefault(8, 0);
        }

        profileManager.getAnyProfile(player).getGearStats().setAllGearStats(attack,health,defense,magic_defense,crit);
        profileManager.getAnyProfile(player).getSkillLevels().setAllSkillLevelBonus(skill_1,skill_2,skill_3,skill_4,skill_5,skill_6,skill_7,skill_8);

    }

    public Map<Object, Integer> getGearStats(MysticaEquipment equipment){

        int attack = 0;
        int health = 0;
        int defense = 0;
        int magic_defense = 0;
        int crit = 0;

        int skill_1 = 0;
        int skill_2 = 0;
        int skill_3 = 0;
        int skill_4 = 0;
        int skill_5 = 0;
        int skill_6 = 0;
        int skill_7 = 0;
        int skill_8 = 0;

        if(equipment != null){
            attack += statCalculator.getBaseStats(equipment).get(StatType.ATTACK);
            health += statCalculator.getBaseStats(equipment).get(StatType.HEALTH);
            defense += statCalculator.getBaseStats(equipment).get(StatType.DEFENSE);
            magic_defense += statCalculator.getBaseStats(equipment).get(StatType.MAGIC_DEFENSE);
            crit += statCalculator.getBaseStats(equipment).get(StatType.CRIT);

            for (StatRoll roll : equipment.getStatRolls()){

                switch (roll.type()){
                    case ATTACK -> attack+=roll.amount();
                    case HEALTH -> health+=roll.amount();
                    case DEFENSE -> defense+=roll.amount();
                    case MAGIC_DEFENSE -> magic_defense+=roll.amount();
                    case CRIT -> crit+=roll.amount();
                }

            }


            for(SkillRoll roll : equipment.getSkillRolls()){
                switch (roll.skillId()){
                    case 1 -> skill_1+=roll.amount();
                    case 2 -> skill_2+= roll.amount();
                    case 3 -> skill_3 += roll.amount();
                    case 4 -> skill_4 += roll.amount();
                    case 5 -> skill_5 += roll.amount();
                    case 6 -> skill_6 += roll.amount();
                    case 7 -> skill_7 += roll.amount();
                    case 8 -> skill_8 += roll.amount();
                }
            }
        }



        Map<Object, Integer> statMap = new HashMap<>();

        statMap.put(StatType.ATTACK, attack);
        statMap.put(StatType.HEALTH, health);
        statMap.put(StatType.DEFENSE, defense);
        statMap.put(StatType.MAGIC_DEFENSE, magic_defense);
        statMap.put(StatType.CRIT, crit);
        statMap.put(1, skill_1);
        statMap.put(2, skill_2);
        statMap.put(3, skill_3);
        statMap.put(4, skill_4);
        statMap.put(5, skill_5);
        statMap.put(6, skill_6);
        statMap.put(7, skill_7);
        statMap.put(8, skill_8);

        return statMap;
    }

}
