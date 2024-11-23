package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.ClassEquipment.*;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClassSetter {

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;
    private final NoneEquipment noneEquipment;
    private final ElementalistEquipment elementalistEquipment;
    private final RangerEquipment rangerEquipment;
    private final MysticEquipment mysticEquipment;
    private final ShadowKnightEquipment shadowKnightEquipment;
    private final PaladinEquipment paladinEquipment;
    private final WarriorEquipment warriorEquipment;
    private final AssassinEquipment assassinEquipment;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;

    public ClassSetter(Mystica main){
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        noneEquipment = new NoneEquipment();
        elementalistEquipment = new ElementalistEquipment();
        rangerEquipment = new RangerEquipment();
        mysticEquipment = new MysticEquipment();
        shadowKnightEquipment = new ShadowKnightEquipment();
        paladinEquipment = new PaladinEquipment();
        warriorEquipment = new WarriorEquipment();
        assassinEquipment = new AssassinEquipment();
        displayWeapons = new DisplayWeapons(main);
        gearReader = new GearReader(main);
    }

    public void setClass(Player player, String clazz){


        Profile playerProfile = profileManager.getAnyProfile(player);

        if(playerProfile.getPlayerClass().equalsIgnoreCase(clazz)){
            player.sendMessage("you are already this class");
            return;
        }


        if(playerProfile.getPlayerClass().equalsIgnoreCase("none")){

            playerProfile.setPlayerClass(clazz);

            switch(clazz.toLowerCase()){
                case "elementalist":{
                    playerProfile.getPlayerEquipment().setWeapon(elementalistEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(elementalistEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(elementalistEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(elementalistEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(elementalistEquipment.getBaseBoots());
                    break;
                }
                case "ranger":{
                    playerProfile.getPlayerEquipment().setWeapon(rangerEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(rangerEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(rangerEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(rangerEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(rangerEquipment.getBaseBoots());
                    break;
                }
                case "mystic":{
                    playerProfile.getPlayerEquipment().setWeapon(mysticEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(mysticEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(mysticEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(mysticEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(mysticEquipment.getBaseBoots());
                    break;
                }
                case "shadow knight":{
                    playerProfile.getPlayerEquipment().setWeapon(shadowKnightEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(shadowKnightEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(shadowKnightEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(shadowKnightEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(shadowKnightEquipment.getBaseBoots());
                    break;
                }
                case "paladin":{
                    playerProfile.getPlayerEquipment().setWeapon(paladinEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(paladinEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(paladinEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(paladinEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(paladinEquipment.getBaseBoots());
                    break;
                }
                case "warrior":{
                    playerProfile.getPlayerEquipment().setWeapon(warriorEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(warriorEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(warriorEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(warriorEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(warriorEquipment.getBaseBoots());
                    break;
                }
                case "assassin":{
                    playerProfile.getPlayerEquipment().setWeapon(assassinEquipment.getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(assassinEquipment.getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(assassinEquipment.getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(assassinEquipment.getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(assassinEquipment.getBaseBoots());
                    break;
                }
                case "none":{
                    playerProfile.getPlayerEquipment().setWeapon(new ItemStack(Material.AIR));
                    playerProfile.getPlayerEquipment().setHelmet(new ItemStack(Material.AIR));
                    playerProfile.getPlayerEquipment().setChestPlate(new ItemStack(Material.AIR));
                    playerProfile.getPlayerEquipment().setLeggings(new ItemStack(Material.AIR));
                    playerProfile.getPlayerEquipment().setBoots(new ItemStack(Material.AIR));
                    playerProfile.setPlayerClass(clazz);
                    profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(), "none", "none");
                    displayWeapons.displayWeapons(player);
                    displayWeapons.displayArmor(player);
                    gearReader.setGearStats(player);
                    return;
                }
            }
        }
        else{

            playerProfile.setPlayerClass(clazz);


            for(ItemStack equipment : playerProfile.getPlayerEquipment().getEquipment()){

                if(equipment == null || equipment.getType().equals(Material.AIR)){
                    continue;
                }

                ItemStack newItem = equipmentManager.swap(player, equipment);

                //Bukkit.getLogger().info("new item " + newItem + " generated");


                ItemMeta selectedMeta = newItem.getItemMeta();
                assert selectedMeta != null;
                List<String> lore = selectedMeta.getLore();
                assert lore != null;
                String equipSlot = lore.get(1);
                equipSlot = equipSlot.replaceAll("§.", "");

                switch (equipSlot.toLowerCase()){
                    case "weapon":{
                        playerProfile.getPlayerEquipment().setWeapon(newItem);
                        break;
                    }
                    case "helmet":{
                        playerProfile.getPlayerEquipment().setHelmet(newItem);
                        break;
                    }
                    case "chestplate":{
                        playerProfile.getPlayerEquipment().setChestPlate(newItem);
                        break;
                    }
                    case "leggings":{
                        playerProfile.getPlayerEquipment().setLeggings(newItem);
                        break;
                    }
                    case "boots":{
                        playerProfile.getPlayerEquipment().setBoots(newItem);
                        break;
                    }
                }

            }

        }


        playerProfile.setPlayerSubclass("none");
        profileManager.getAnyProfile(player).getStats().setLevelStats(profileManager.getAnyProfile(player).getStats().getLevel(), clazz, "none");
        player.sendMessage("You are now a(n) " + clazz);


        displayWeapons.displayWeapons(player);
        displayWeapons.displayArmor(player);
        gearReader.setGearStats(player);

    }


}
