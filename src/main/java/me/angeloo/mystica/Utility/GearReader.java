package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if(equipment.getWeapon() instanceof MysticaEquipment weapon){
            attack += weapon.getWeaponBaseAttack(weapon.getLevel());
            health += weapon.getWeaponBaseHealth(weapon.getLevel());
            defense += weapon.getWeaponBaseDefense(weapon.getLevel());
            magic_defense += weapon.getWeaponBaseDefense(weapon.getLevel());

            if(weapon.getHighStat() != null){
                switch (weapon.getHighStat()){
                    case Attack -> {
                        attack += weapon.getHighStatAmount(weapon.getHighStat(), weapon.getLevel());
                    }
                    case Health -> {
                        health += weapon.getHighStatAmount(weapon.getHighStat(), weapon.getLevel());
                    }
                    case Defense -> {
                        defense += weapon.getHighStatAmount(weapon.getHighStat(), weapon.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += weapon.getHighStatAmount(weapon.getHighStat(), weapon.getLevel());
                    }
                    case Crit -> {
                        crit += weapon.getHighStatAmount(weapon.getHighStat(), weapon.getLevel());
                    }
                }
            }

            if(weapon.getLowStat() != null){
                switch (weapon.getLowStat()){
                    case Attack -> {
                        attack += weapon.getLowStatAmount(weapon.getLowStat(), weapon.getLevel());
                    }
                    case Health -> {
                        health += weapon.getLowStatAmount(weapon.getLowStat(), weapon.getLevel());
                    }
                    case Defense -> {
                        defense += weapon.getLowStatAmount(weapon.getLowStat(), weapon.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += weapon.getLowStatAmount(weapon.getLowStat(), weapon.getLevel());
                    }
                    case Crit -> {
                        crit += weapon.getLowStatAmount(weapon.getLowStat(), weapon.getLevel());
                    }
                }
            }

            if(weapon.getSkillOne() != null){
                switch (weapon.getSkillOne().get(0)) {
                    case 1 -> {
                        skill_1 += weapon.getSkillOne().get(1);
                    }
                    case 2 -> {
                        skill_2 += weapon.getSkillOne().get(1);
                    }
                    case 3 -> {
                        skill_3 += weapon.getSkillOne().get(1);
                    }
                    case 4 -> {
                        skill_4 += weapon.getSkillOne().get(1);
                    }
                    case 5 -> {
                        skill_5 += weapon.getSkillOne().get(1);
                    }
                    case 6 -> {
                        skill_6 += weapon.getSkillOne().get(1);
                    }
                    case 7 -> {
                        skill_7 += weapon.getSkillOne().get(1);
                    }
                    case 8 -> {
                        skill_8 += weapon.getSkillOne().get(1);
                    }
                }
            }

            if(weapon.getSkillTwo() != null){
                switch (weapon.getSkillTwo().get(0)) {
                    case 1 -> {
                        skill_1 += weapon.getSkillTwo().get(1);
                    }
                    case 2 -> {
                        skill_2 += weapon.getSkillTwo().get(1);
                    }
                    case 3 -> {
                        skill_3 += weapon.getSkillTwo().get(1);
                    }
                    case 4 -> {
                        skill_4 += weapon.getSkillTwo().get(1);
                    }
                    case 5 -> {
                        skill_5 += weapon.getSkillTwo().get(1);
                    }
                    case 6 -> {
                        skill_6 += weapon.getSkillTwo().get(1);
                    }
                    case 7 -> {
                        skill_7 += weapon.getSkillTwo().get(1);
                    }
                    case 8 -> {
                        skill_8 += weapon.getSkillTwo().get(1);
                    }
                }
            }

        }

        if(equipment.getHelmet() instanceof MysticaEquipment helmet){

            health += helmet.getHelmetBaseHealth(helmet.getLevel());

            if(helmet.getHighStat() != null){
                switch (helmet.getHighStat()){
                    case Attack -> {
                        attack += helmet.getHighStatAmount(helmet.getHighStat(), helmet.getLevel());
                    }
                    case Health -> {
                        health += helmet.getHighStatAmount(helmet.getHighStat(), helmet.getLevel());
                    }
                    case Defense -> {
                        defense += helmet.getHighStatAmount(helmet.getHighStat(), helmet.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += helmet.getHighStatAmount(helmet.getHighStat(), helmet.getLevel());
                    }
                    case Crit -> {
                        crit += helmet.getHighStatAmount(helmet.getHighStat(), helmet.getLevel());
                    }
                }
            }

            if(helmet.getLowStat() != null){
                switch (helmet.getLowStat()){
                    case Attack -> {
                        attack += helmet.getLowStatAmount(helmet.getLowStat(), helmet.getLevel());
                    }
                    case Health -> {
                        health += helmet.getLowStatAmount(helmet.getLowStat(), helmet.getLevel());
                    }
                    case Defense -> {
                        defense += helmet.getLowStatAmount(helmet.getLowStat(), helmet.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += helmet.getLowStatAmount(helmet.getLowStat(), helmet.getLevel());
                    }
                    case Crit -> {
                        crit += helmet.getLowStatAmount(helmet.getLowStat(), helmet.getLevel());
                    }
                }
            }

            if(helmet.getSkillOne() != null){
                switch (helmet.getSkillOne().get(0)) {
                    case 1 -> {
                        skill_1 += helmet.getSkillOne().get(1);
                    }
                    case 2 -> {
                        skill_2 += helmet.getSkillOne().get(1);
                    }
                    case 3 -> {
                        skill_3 += helmet.getSkillOne().get(1);
                    }
                    case 4 -> {
                        skill_4 += helmet.getSkillOne().get(1);
                    }
                    case 5 -> {
                        skill_5 += helmet.getSkillOne().get(1);
                    }
                    case 6 -> {
                        skill_6 += helmet.getSkillOne().get(1);
                    }
                    case 7 -> {
                        skill_7 += helmet.getSkillOne().get(1);
                    }
                    case 8 -> {
                        skill_8 += helmet.getSkillOne().get(1);
                    }
                }
            }

            if(helmet.getSkillTwo() != null){
                switch (helmet.getSkillTwo().get(0)) {
                    case 1 -> {
                        skill_1 += helmet.getSkillTwo().get(1);
                    }
                    case 2 -> {
                        skill_2 += helmet.getSkillTwo().get(1);
                    }
                    case 3 -> {
                        skill_3 += helmet.getSkillTwo().get(1);
                    }
                    case 4 -> {
                        skill_4 += helmet.getSkillTwo().get(1);
                    }
                    case 5 -> {
                        skill_5 += helmet.getSkillTwo().get(1);
                    }
                    case 6 -> {
                        skill_6 += helmet.getSkillTwo().get(1);
                    }
                    case 7 -> {
                        skill_7 += helmet.getSkillTwo().get(1);
                    }
                    case 8 -> {
                        skill_8 += helmet.getSkillTwo().get(1);
                    }
                }
            }

        }

        if(equipment.getChestPlate() instanceof MysticaEquipment chestplate){

            health += chestplate.getChestBaseHealth(chestplate.getLevel());
            defense += chestplate.getChestBaseDefense(chestplate.getLevel());
            magic_defense += chestplate.getChestBaseDefense(chestplate.getLevel());

            if(chestplate.getHighStat() != null){
                switch (chestplate.getHighStat()){
                    case Attack -> {
                        attack += chestplate.getHighStatAmount(chestplate.getHighStat(), chestplate.getLevel());
                    }
                    case Health -> {
                        health += chestplate.getHighStatAmount(chestplate.getHighStat(), chestplate.getLevel());
                    }
                    case Defense -> {
                        defense += chestplate.getHighStatAmount(chestplate.getHighStat(), chestplate.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += chestplate.getHighStatAmount(chestplate.getHighStat(), chestplate.getLevel());
                    }
                    case Crit -> {
                        crit += chestplate.getHighStatAmount(chestplate.getHighStat(), chestplate.getLevel());
                    }
                }
            }

            if(chestplate.getLowStat() != null){
                switch (chestplate.getLowStat()){
                    case Attack -> {
                        attack += chestplate.getLowStatAmount(chestplate.getLowStat(), chestplate.getLevel());
                    }
                    case Health -> {
                        health += chestplate.getLowStatAmount(chestplate.getLowStat(), chestplate.getLevel());
                    }
                    case Defense -> {
                        defense += chestplate.getLowStatAmount(chestplate.getLowStat(), chestplate.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += chestplate.getLowStatAmount(chestplate.getLowStat(), chestplate.getLevel());
                    }
                    case Crit -> {
                        crit += chestplate.getLowStatAmount(chestplate.getLowStat(), chestplate.getLevel());
                    }
                }
            }

            if(chestplate.getSkillOne() != null){
                switch (chestplate.getSkillOne().get(0)) {
                    case 1 -> {
                        skill_1 += chestplate.getSkillOne().get(1);
                    }
                    case 2 -> {
                        skill_2 += chestplate.getSkillOne().get(1);
                    }
                    case 3 -> {
                        skill_3 += chestplate.getSkillOne().get(1);
                    }
                    case 4 -> {
                        skill_4 += chestplate.getSkillOne().get(1);
                    }
                    case 5 -> {
                        skill_5 += chestplate.getSkillOne().get(1);
                    }
                    case 6 -> {
                        skill_6 += chestplate.getSkillOne().get(1);
                    }
                    case 7 -> {
                        skill_7 += chestplate.getSkillOne().get(1);
                    }
                    case 8 -> {
                        skill_8 += chestplate.getSkillOne().get(1);
                    }
                }
            }

            if(chestplate.getSkillTwo() != null){
                switch (chestplate.getSkillTwo().get(0)) {
                    case 1 -> {
                        skill_1 += chestplate.getSkillTwo().get(1);
                    }
                    case 2 -> {
                        skill_2 += chestplate.getSkillTwo().get(1);
                    }
                    case 3 -> {
                        skill_3 += chestplate.getSkillTwo().get(1);
                    }
                    case 4 -> {
                        skill_4 += chestplate.getSkillTwo().get(1);
                    }
                    case 5 -> {
                        skill_5 += chestplate.getSkillTwo().get(1);
                    }
                    case 6 -> {
                        skill_6 += chestplate.getSkillTwo().get(1);
                    }
                    case 7 -> {
                        skill_7 += chestplate.getSkillTwo().get(1);
                    }
                    case 8 -> {
                        skill_8 += chestplate.getSkillTwo().get(1);
                    }
                }
            }

        }

        if(equipment.getLeggings() instanceof MysticaEquipment leggings){

            attack += leggings.getLeggingBaseAttack(leggings.getLevel());

            if(leggings.getHighStat() != null){
                switch (leggings.getHighStat()){
                    case Attack -> {
                        attack += leggings.getHighStatAmount(leggings.getHighStat(), leggings.getLevel());
                    }
                    case Health -> {
                        health += leggings.getHighStatAmount(leggings.getHighStat(), leggings.getLevel());
                    }
                    case Defense -> {
                        defense += leggings.getHighStatAmount(leggings.getHighStat(), leggings.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += leggings.getHighStatAmount(leggings.getHighStat(), leggings.getLevel());
                    }
                    case Crit -> {
                        crit += leggings.getHighStatAmount(leggings.getHighStat(), leggings.getLevel());
                    }
                }
            }

            if(leggings.getLowStat() != null){
                switch (leggings.getLowStat()){
                    case Attack -> {
                        attack += leggings.getLowStatAmount(leggings.getLowStat(), leggings.getLevel());
                    }
                    case Health -> {
                        health += leggings.getLowStatAmount(leggings.getLowStat(), leggings.getLevel());
                    }
                    case Defense -> {
                        defense += leggings.getLowStatAmount(leggings.getLowStat(), leggings.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += leggings.getLowStatAmount(leggings.getLowStat(), leggings.getLevel());
                    }
                    case Crit -> {
                        crit += leggings.getLowStatAmount(leggings.getLowStat(), leggings.getLevel());
                    }
                }
            }

            if(leggings.getSkillOne() != null){
                switch (leggings.getSkillOne().get(0)) {
                    case 1 -> {
                        skill_1 += leggings.getSkillOne().get(1);
                    }
                    case 2 -> {
                        skill_2 += leggings.getSkillOne().get(1);
                    }
                    case 3 -> {
                        skill_3 += leggings.getSkillOne().get(1);
                    }
                    case 4 -> {
                        skill_4 += leggings.getSkillOne().get(1);
                    }
                    case 5 -> {
                        skill_5 += leggings.getSkillOne().get(1);
                    }
                    case 6 -> {
                        skill_6 += leggings.getSkillOne().get(1);
                    }
                    case 7 -> {
                        skill_7 += leggings.getSkillOne().get(1);
                    }
                    case 8 -> {
                        skill_8 += leggings.getSkillOne().get(1);
                    }
                }
            }

            if(leggings.getSkillTwo() != null){
                switch (leggings.getSkillTwo().get(0)) {
                    case 1 -> {
                        skill_1 += leggings.getSkillTwo().get(1);
                    }
                    case 2 -> {
                        skill_2 += leggings.getSkillTwo().get(1);
                    }
                    case 3 -> {
                        skill_3 += leggings.getSkillTwo().get(1);
                    }
                    case 4 -> {
                        skill_4 += leggings.getSkillTwo().get(1);
                    }
                    case 5 -> {
                        skill_5 += leggings.getSkillTwo().get(1);
                    }
                    case 6 -> {
                        skill_6 += leggings.getSkillTwo().get(1);
                    }
                    case 7 -> {
                        skill_7 += leggings.getSkillTwo().get(1);
                    }
                    case 8 -> {
                        skill_8 += leggings.getSkillTwo().get(1);
                    }
                }
            }

        }

        if(equipment.getBoots() instanceof MysticaEquipment boots){

            attack += boots.getBootsBaseAttack(boots.getLevel());

            if(boots.getHighStat() != null){
                switch (boots.getHighStat()){
                    case Attack -> {
                        attack += boots.getHighStatAmount(boots.getHighStat(), boots.getLevel());
                    }
                    case Health -> {
                        health += boots.getHighStatAmount(boots.getHighStat(), boots.getLevel());
                    }
                    case Defense -> {
                        defense += boots.getHighStatAmount(boots.getHighStat(), boots.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += boots.getHighStatAmount(boots.getHighStat(), boots.getLevel());
                    }
                    case Crit -> {
                        crit += boots.getHighStatAmount(boots.getHighStat(), boots.getLevel());
                    }
                }
            }

            if(boots.getLowStat() != null){
                switch (boots.getLowStat()){
                    case Attack -> {
                        attack += boots.getLowStatAmount(boots.getLowStat(), boots.getLevel());
                    }
                    case Health -> {
                        health += boots.getLowStatAmount(boots.getLowStat(), boots.getLevel());
                    }
                    case Defense -> {
                        defense += boots.getLowStatAmount(boots.getLowStat(), boots.getLevel());
                    }
                    case Magic_Defense -> {
                        magic_defense += boots.getLowStatAmount(boots.getLowStat(), boots.getLevel());
                    }
                    case Crit -> {
                        crit += boots.getLowStatAmount(boots.getLowStat(), boots.getLevel());
                    }
                }
            }

            if(boots.getSkillOne() != null){
                switch (boots.getSkillOne().get(0)) {
                    case 1 -> {
                        skill_1 += boots.getSkillOne().get(1);
                    }
                    case 2 -> {
                        skill_2 += boots.getSkillOne().get(1);
                    }
                    case 3 -> {
                        skill_3 += boots.getSkillOne().get(1);
                    }
                    case 4 -> {
                        skill_4 += boots.getSkillOne().get(1);
                    }
                    case 5 -> {
                        skill_5 += boots.getSkillOne().get(1);
                    }
                    case 6 -> {
                        skill_6 += boots.getSkillOne().get(1);
                    }
                    case 7 -> {
                        skill_7 += boots.getSkillOne().get(1);
                    }
                    case 8 -> {
                        skill_8 += boots.getSkillOne().get(1);
                    }
                }
            }

            if(boots.getSkillTwo() != null){
                switch (boots.getSkillTwo().get(0)) {
                    case 1 -> {
                        skill_1 += boots.getSkillTwo().get(1);
                    }
                    case 2 -> {
                        skill_2 += boots.getSkillTwo().get(1);
                    }
                    case 3 -> {
                        skill_3 += boots.getSkillTwo().get(1);
                    }
                    case 4 -> {
                        skill_4 += boots.getSkillTwo().get(1);
                    }
                    case 5 -> {
                        skill_5 += boots.getSkillTwo().get(1);
                    }
                    case 6 -> {
                        skill_6 += boots.getSkillTwo().get(1);
                    }
                    case 7 -> {
                        skill_7 += boots.getSkillTwo().get(1);
                    }
                    case 8 -> {
                        skill_8 += boots.getSkillTwo().get(1);
                    }
                }
            }
        }


        profileManager.getAnyProfile(player).getGearStats().setAllGearStats(attack,health,defense,magic_defense,crit);
        profileManager.getAnyProfile(player).getSkillLevels().setAllSkillLevelBonus(skill_1,skill_2,skill_3,skill_4,skill_5,skill_6,skill_7,skill_8);

    }

}
