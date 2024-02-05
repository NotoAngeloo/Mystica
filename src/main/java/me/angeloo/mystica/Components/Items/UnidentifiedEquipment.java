package me.angeloo.mystica.Components.Items;

import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnidentifiedEquipment extends ItemStack {

    public UnidentifiedEquipment(int level){
        super(Material.IRON_INGOT);
        ItemMeta meta = this.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.of(new Color(218, 133, 36)) + "Unidentified Equipment");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.of(new Color(176, 159, 109)) + "Level: " + ChatColor.of(new Color(255,255,255)) + level);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(new NamespacedKey(Mystica.getPlugin(), "uuid"),PersistentDataType.STRING, UUID.randomUUID().toString());
        this.setItemMeta(meta);
    }

}
