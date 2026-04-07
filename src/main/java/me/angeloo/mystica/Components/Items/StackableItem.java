package me.angeloo.mystica.Components.Items;


import com.google.gson.Gson;
import me.angeloo.mystica.Mystica;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StackableItem extends MysticaItem{

    protected int amount;

    public StackableItem(int amount){

        this.amount = amount;
    }

    public abstract Material getBaseMaterial();
    public abstract List<String> getLore();
    public abstract String getDisplayName();
    public abstract int getCustomModelData();

    @Override
    public MysticaItemFormat format() {
        return MysticaItemFormat.STACKABLE;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ItemStack build(){
        ItemStack item = new ItemStack(getBaseMaterial());
        ItemMeta meta = item.getItemMeta();

        if(meta == null){
            return item;
        }

        int amount = this.amount;

        if(amount > 64){
            amount = 64;
        }

        item.setAmount(amount);

        meta.setDisplayName(getDisplayName());

        meta.setLore(getLore());

        meta.setCustomModelData(getCustomModelData());

        NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "stackable_data");
        Gson gson = new Gson();
        String json = gson.toJson(this);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json);

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> map = new HashMap<>();
        map.put("identifier",identifier());
        map.put("format", format().name());
        map.put("amount",this.amount);

        return map;
    }


}
