package me.angeloo.mystica.Tasks;

import me.angeloo.mystica.Components.Items.RezItem;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.CustomItemConverter;
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
    private final CustomItemConverter customItemConverter;

    public RezTick(Mystica main){
        profileManager = main.getProfileManager();
        customItemConverter = new CustomItemConverter();
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
                ItemStack rezItem = customItemConverter.convert(new RezItem(), 1);
                player.getInventory().setItem(4, rezItem);
            }
        }
    }
}
