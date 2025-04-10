package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Managers.ItemManager;
import me.angeloo.mystica.Managers.EquipmentManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClassSetter {

    private final ProfileManager profileManager;
    private final EquipmentManager equipmentManager;
    private final ItemManager itemManager;
    private final DisplayWeapons displayWeapons;
    private final GearReader gearReader;

    public ClassSetter(Mystica main){
        profileManager = main.getProfileManager();
        equipmentManager = new EquipmentManager(main);
        itemManager = main.getClassEquipmentManager();
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
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getElementalistEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getElementalistEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getElementalistEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getElementalistEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getElementalistEquipment().getBaseBoots());
                    break;
                }
                case "ranger":{
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getRangerEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getRangerEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getRangerEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getRangerEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getRangerEquipment().getBaseBoots());
                    break;
                }
                case "mystic":{
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getMysticEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getMysticEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getMysticEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getMysticEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getMysticEquipment().getBaseBoots());
                    break;
                }
                case "shadow knight":{
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getShadowKnightEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getShadowKnightEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getShadowKnightEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getShadowKnightEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getShadowKnightEquipment().getBaseBoots());
                    break;
                }
                case "paladin":{
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getPaladinEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getPaladinEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getPaladinEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getPaladinEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getPaladinEquipment().getBaseBoots());
                    break;
                }
                case "warrior":{
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getWarriorEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getWarriorEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getWarriorEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getWarriorEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getWarriorEquipment().getBaseBoots());
                    break;
                }
                case "assassin":{
                    playerProfile.getPlayerEquipment().setWeapon(itemManager.getAssassinEquipment().getBaseWeapon());
                    playerProfile.getPlayerEquipment().setHelmet(itemManager.getAssassinEquipment().getBaseHelmet());
                    playerProfile.getPlayerEquipment().setChestPlate(itemManager.getAssassinEquipment().getBaseChestPlate());
                    playerProfile.getPlayerEquipment().setLeggings(itemManager.getAssassinEquipment().getBaseLeggings());
                    playerProfile.getPlayerEquipment().setBoots(itemManager.getAssassinEquipment().getBaseBoots());
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
