package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GearReader {

    private final ProfileManager profileManager;

    public GearReader(Mystica main){
        profileManager = main.getProfileManager();
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

            attack+=stats.getOrDefault(MysticaEquipment.StatType.Attack, 0);
            health+=stats.getOrDefault(MysticaEquipment.StatType.Health, 0);
            defense+=stats.getOrDefault(MysticaEquipment.StatType.Defense, 0);
            magic_defense+=stats.getOrDefault(MysticaEquipment.StatType.Magic_Defense, 0);
            crit+=stats.getOrDefault(MysticaEquipment.StatType.Crit, 0);

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
            switch (equipment.getEquipmentSlot()){
                case WEAPON -> {
                    attack += equipment.getWeaponBaseAttack(equipment.getLevel());
                    health += equipment.getWeaponBaseHealth(equipment.getLevel());
                    defense += equipment.getWeaponBaseDefense(equipment.getLevel());
                    magic_defense += equipment.getWeaponBaseDefense(equipment.getLevel());
                }
                case HEAD -> {
                    health += equipment.getHelmetBaseHealth(equipment.getLevel());
                }
                case CHEST -> {
                    health += equipment.getChestBaseHealth(equipment.getLevel());
                    defense += equipment.getChestBaseDefense(equipment.getLevel());
                    magic_defense += equipment.getChestBaseDefense(equipment.getLevel());
                }
                case LEGS -> {
                    attack += equipment.getLeggingBaseAttack(equipment.getLevel());
                }
                case BOOTS -> {
                    attack += equipment.getBootsBaseAttack(equipment.getLevel());
                }
            }

            if(equipment.getHighStat() != null){
                switch (equipment.getHighStat()){
                    case Attack -> {
                        attack += equipment.getHighStatAmount(equipment.getHighStat(), equipment.getLevel());
                    }
                    case Health -> {
                        health += equipment.getHighStatAmount(equipment.getHighStat(), equipment.getLevel());
                    }
                    case Defense -> {
                        defense += equipment.getHighStatAmount(equipment.getHighStat(), equipment.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += equipment.getHighStatAmount(equipment.getHighStat(), equipment.getLevel());
                    }
                    case Crit -> {
                        crit += equipment.getHighStatAmount(equipment.getHighStat(), equipment.getLevel());
                    }
                }
            }

            if(equipment.getLowStat() != null){
                switch (equipment.getLowStat()){
                    case Attack -> {
                        attack += equipment.getLowStatAmount(equipment.getLowStat(), equipment.getLevel());
                    }
                    case Health -> {
                        health += equipment.getLowStatAmount(equipment.getLowStat(), equipment.getLevel());
                    }
                    case Defense -> {
                        defense += equipment.getLowStatAmount(equipment.getLowStat(), equipment.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += equipment.getLowStatAmount(equipment.getLowStat(), equipment.getLevel());
                    }
                    case Crit -> {
                        crit += equipment.getLowStatAmount(equipment.getLowStat(), equipment.getLevel());
                    }
                }
            }

            if(equipment.getSkillOne() != null){
                switch (equipment.getSkillOne().get(0)) {
                    case 1 -> {
                        skill_1 += equipment.getSkillOne().get(1);
                    }
                    case 2 -> {
                        skill_2 += equipment.getSkillOne().get(1);
                    }
                    case 3 -> {
                        skill_3 += equipment.getSkillOne().get(1);
                    }
                    case 4 -> {
                        skill_4 += equipment.getSkillOne().get(1);
                    }
                    case 5 -> {
                        skill_5 += equipment.getSkillOne().get(1);
                    }
                    case 6 -> {
                        skill_6 += equipment.getSkillOne().get(1);
                    }
                    case 7 -> {
                        skill_7 += equipment.getSkillOne().get(1);
                    }
                    case 8 -> {
                        skill_8 += equipment.getSkillOne().get(1);
                    }
                }
            }

            if(equipment.getSkillTwo() != null){
                switch (equipment.getSkillTwo().get(0)) {
                    case 1 -> {
                        skill_1 += equipment.getSkillTwo().get(1);
                    }
                    case 2 -> {
                        skill_2 += equipment.getSkillTwo().get(1);
                    }
                    case 3 -> {
                        skill_3 += equipment.getSkillTwo().get(1);
                    }
                    case 4 -> {
                        skill_4 += equipment.getSkillTwo().get(1);
                    }
                    case 5 -> {
                        skill_5 += equipment.getSkillTwo().get(1);
                    }
                    case 6 -> {
                        skill_6 += equipment.getSkillTwo().get(1);
                    }
                    case 7 -> {
                        skill_7 += equipment.getSkillTwo().get(1);
                    }
                    case 8 -> {
                        skill_8 += equipment.getSkillTwo().get(1);
                    }
                }
            }
        }



        Map<Object, Integer> statMap = new HashMap<>();

        statMap.put(MysticaEquipment.StatType.Attack, attack);
        statMap.put(MysticaEquipment.StatType.Health, health);
        statMap.put(MysticaEquipment.StatType.Defense, defense);
        statMap.put(MysticaEquipment.StatType.Magic_Defense, magic_defense);
        statMap.put(MysticaEquipment.StatType.Crit, crit);
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
