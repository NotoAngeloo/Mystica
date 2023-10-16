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

public class BuyInvSlotsInventory {

    private final ProfileManager profileManager;

    public BuyInvSlotsInventory(Mystica main){
        profileManager = main.getProfileManager();
    }

    public Inventory openBuyInv(Player player){

        int slots = profileManager.getAnyProfile(player).getPlayerBag().getNumUnlocks();
        int bal = profileManager.getAnyProfile(player).getPoints().getBal();
        int price = (20 + (20 * slots));

        Inventory inv = Bukkit.createInventory(null, 9 ,"Purchase More Space?");

        inv.setItem(0, getItem(new ItemStack(Material.SUNFLOWER), "Balance", "$"+bal ));
        inv.setItem(8, getItem(new ItemStack(Material.SPECTRAL_ARROW), "Bag Slots", String.valueOf(slots)));

        inv.setItem(3, getItem(new ItemStack(Material.EMERALD), "Buy", "$"+price));
        inv.setItem(5, getItem(new ItemStack(Material.BARRIER), "Cancel"));
        return inv;
    }

    private ItemStack getItem(ItemStack item, String name, String... lore){
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
