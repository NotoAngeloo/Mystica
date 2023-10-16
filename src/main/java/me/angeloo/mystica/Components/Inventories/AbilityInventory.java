package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AbilityInventory {

    private final ProfileManager profileManager;
    private final AllSkillItems allSkillItems;

    public AbilityInventory(Mystica main){
        profileManager = main.getProfileManager();
        allSkillItems = new AllSkillItems(main);
    }

    public Inventory openAbilityInventory(Player player, ItemStack skill, boolean equipping){

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();

        int size;

        size = 9*4;

        if(equipping){
            size = 9*5;
        }

        Inventory inv = Bukkit.createInventory(null, size, "Active Skill");

        for(int i=0;i<inv.getSize();i++){

            if(i <= 8 || i ==18){
                continue;
            }

            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, " "));
        }

        inv.setItem(26, getItem(Material.EMERALD, "Specializations"));


        inv.setItem(0, allSkillItems.getPlayerSkill(player, 1));
        inv.setItem(1, allSkillItems.getPlayerSkill(player, 2));
        inv.setItem(2, allSkillItems.getPlayerSkill(player, 3));
        inv.setItem(3, allSkillItems.getPlayerSkill(player, 4));
        inv.setItem(4, allSkillItems.getPlayerSkill(player, 5));
        inv.setItem(5, allSkillItems.getPlayerSkill(player, 6));
        inv.setItem(6, allSkillItems.getPlayerSkill(player, 7));
        inv.setItem(7, allSkillItems.getPlayerSkill(player, 8));

        inv.setItem(8, allSkillItems.getUltimate(player));

        //the selected item
        inv.setItem(18, skill);

        if(!equipping){
            inv.setItem(22, getItem(Material.LIME_DYE, "See Equipped Skills"));
        }
        else{
            inv.setItem(22, getItem(Material.RED_DYE, "Hide Equipped Skills"));
        }

        if(equipping){
            inv.setItem(36, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[0]));
            inv.setItem(37, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[1]));
            inv.setItem(38, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[2]));
            inv.setItem(39, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[3]));
            inv.setItem(40, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[4]));
            inv.setItem(41, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[5]));
            inv.setItem(42, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[6]));
            inv.setItem(43, allSkillItems.getPlayerSkill(player, equipSkills.getAnySlot()[7]));


            //inv slot 35 occupied by ultimate
            inv.setItem(44, allSkillItems.getUltimate(player));
        }


        return inv;
    }

    private ItemStack getItem(Material material, String name, String ... lore){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        List<String> lores = new ArrayList<>();

        for (String s : lore){
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(lores);

        item.setItemMeta(meta);
        return item;
    }

}
