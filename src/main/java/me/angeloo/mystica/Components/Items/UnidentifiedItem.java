package me.angeloo.mystica.Components.Items;

import com.google.gson.Gson;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.EquipmentSlot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.angeloo.mystica.Mystica.*;

public class UnidentifiedItem extends MysticaItem{


    private final EquipmentSlot equipmentSlot;
    private final int level;
    private final int tier;

    public UnidentifiedItem(EquipmentSlot equipmentSlot, int level, int tier){
        this.equipmentSlot = equipmentSlot;
        this.level = level;
        this.tier = tier;
    }

    @Override
    public MysticaItemFormat format() {
        return MysticaItemFormat.UNIDENTIFIED;
    }

    @Override
    public String identifier() {
        return "Unidentified " + equipmentSlot.name();
    }

    @Override
    public ItemStack build() {
        ItemStack item = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lores = new ArrayList<>();

        Color color = commonColor;

        switch (this.tier) {
            case 1 -> {
            }
            case 2 -> {
                color = uncommonColor;
            }
            case 3 -> {
                color = rareColor;
            }
        }

        switch (this.equipmentSlot){
            case WEAPON -> {
                meta.setDisplayName(ChatColor.of(color) + "Unidentified Weapon");
            }
            case HEAD -> {
                meta.setDisplayName(ChatColor.of(color) + "Unidentified Helmet");
            }
            case CHEST -> {
                meta.setDisplayName(ChatColor.of(color) + "Unidentified Chestplate");
            }
            case LEGS -> {
                meta.setDisplayName(ChatColor.of(color) + "Unidentified Leggings");
            }
            case BOOTS -> {
                meta.setDisplayName(ChatColor.of(color) + "Unidentified Boots");
            }
        }

        lores.add(ChatColor.of(menuColor) + "Level: " + level);
        lores.add(ChatColor.of(menuColor) + "Tier: " + tier);

        NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "unidentified_data");
        Gson gson = new Gson();
        String json = gson.toJson(this);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json);

        if(tier==1){
            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(menuColor) + "Bonus Attribute (Random)");
        lores.add(ChatColor.of(uncommonColor) + "Attack");
        lores.add(ChatColor.of(uncommonColor) + "Health");
        lores.add(ChatColor.of(uncommonColor) + "Defense");
        lores.add(ChatColor.of(uncommonColor) + "Magic Defense");
        lores.add(ChatColor.of(uncommonColor) + "Crit");

        if(tier==2){
            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(menuColor) + "Special Attribute");
        lores.add(ChatColor.of(rareColor) + "2x Skill Level 1-5");



        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("level", this.level);
        map.put("tier", this.tier);
        map.put("slot",this.equipmentSlot.name());
        map.put("format", format().name());

        return map;
    }

    public static UnidentifiedItem deserialize(Map<String, Object> map){

        EquipmentSlot slot = EquipmentSlot.valueOf((String)map.get("slot"));
        int level = (int) map.get("level");
        int tier = (int) map.get("tier");

        return new UnidentifiedItem(slot,level,tier);
    }
}
