package me.angeloo.mystica.Components.Inventories;

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

public class BossLevelInv {

    private final ProfileManager profileManager;

    public BossLevelInv(Mystica main){
        profileManager = main.getProfileManager();
    }

    public Inventory openBossLevelInv(Player player){

        Inventory inv = Bukkit.createInventory(null, 9, "Change Boss Level");

        int level = profileManager.getAnyProfile(player).getPlayerBossLevel().getBossLevel();

        inv.setItem(4, getItem(new ItemStack(Material.CLOCK), "Level", String.valueOf(level)));

        inv.setItem(5, getItem(new ItemStack(Material.ARROW), "Increase"));
        inv.setItem(3, getItem(new ItemStack(Material.ARROW), "Decrease"));

        return inv;
    }

    private ItemStack getItem(ItemStack item, String name, String... lore){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
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
