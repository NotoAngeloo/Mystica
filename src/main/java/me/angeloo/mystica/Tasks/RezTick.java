package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RezTick extends BukkitRunnable {

    private final ProfileManager profileManager;

    public RezTick(Mystica main){
        profileManager = main.getProfileManager();
    }

    @Override
    public void run(){
        for(Player player : Bukkit.getOnlinePlayers()){

            boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(!deathStatus){
                continue;
            }


            deathStatus = profileManager.getAnyProfile(player).getIfDead();

            if(deathStatus){
                //give them items that rez them
                ItemStack rezItem = new ItemStack(Material.ENDER_EYE);
                ItemMeta rezMeta = rezItem.getItemMeta();
                assert rezMeta != null;
                rezMeta.setDisplayName("Revive");
                List<String> lore = new ArrayList<>();
                lore.add("Right Click to Revive");
                rezMeta.setLore(lore);
                rezItem.setItemMeta(rezMeta);
                player.getInventory().clear();
                player.setItemOnCursor(null);
                player.getInventory().setItem(4, rezItem);
            }
        }
    }
}
