package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassEquipment.NoneEquipment;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.HelpfulHintEvent;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageHealthBoard;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.StatusDisplayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


import java.util.*;
import java.util.List;

public class CombatManager {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final DpsManager dpsManager;
    private final DamageHealthBoard damageHealthBoard;

    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        dpsManager = main.getDpsManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        damageHealthBoard = main.getDamageHealthBoard();
    }


    public void startCombatTimer(Player player){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            player.sendMessage("You are now in combat");

            profileManager.getAnyProfile(player).setSavedInv(player.getInventory().getContents());
            player.getInventory().clear();

            DisplayWeapons displayWeapons = new DisplayWeapons(main);
            displayWeapons.displayArmor(player);

            player.getInventory().setHeldItemSlot(0);

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(playerEquipment.getWeapon() != null){
                player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
            }
            else{
                player.getInventory().setItemInMainHand(new NoneEquipment().getBaseWeapon());
            }

            if (playerEquipment.getOffhand() != null){
                player.getInventory().setItemInOffHand(playerEquipment.getOffhand());
            }

            Bukkit.getServer().getPluginManager().callEvent(new HelpfulHintEvent(player, "combatend"));
            player.getInventory().setItem(13, getItem(new ItemStack(Material.BARRIER), "Exit Combat"));
            cooldownDisplayer.initializeItems(player);

            /*PluginManager pluginManager = Bukkit.getPluginManager();
            Plugin interactions =  pluginManager.getPlugin("interactions");
            if (interactions != null && interactions.isEnabled()) {
                Server server = Bukkit.getServer();

                server.dispatchCommand(server.getConsoleSender(), "interactions stop " + player.getName());
            }*/

        }

        profileManager.getAnyProfile(player).setIfInCombat(true);
        lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
    }


    private long getLastCalledCombat(Player player){

        if(!lastCalledCombat.containsKey(player.getUniqueId())){
            lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastCalledCombat.get(player.getUniqueId());
    }


    public void tryToEndCombat(Player player){

        if(canLeaveCombat(player)){
            forceCombatEnd(player);
        }
        else{
            player.sendMessage(((10000 -(System.currentTimeMillis() - getLastCalledCombat(player)))/1000) + " seconds left");
        }

    }

    public boolean canLeaveCombat(Player player){

        long currentTime = System.currentTimeMillis();
        long lastCalled = getLastCalledCombat(player);

        return currentTime - lastCalled > 10000;
    }

    public void forceCombatEnd(Player player){

        profileManager.getAnyProfile(player).setIfInCombat(false);

        player.getInventory().clear(13);

        player.sendMessage("You are no longer in combat");

        //and restore their inventories

        if(!profileManager.getAnyProfile(player).getIfDead()){

            player.setInvisible(false);

            player.getInventory().clear();

            ItemStack[] savedInv = profileManager.getAnyProfile(player).getSavedInv();

            boolean allNull = true;
            for(ItemStack item : savedInv){
                if(item != null){
                    allNull = false;
                    break;
                }
            }

            if(!allNull){
                player.getInventory().setContents(savedInv);
                profileManager.getAnyProfile(player).removeSavedInv();
            }
        }

        dpsManager.removeDps(player);
        abilityManager.resetAbilityBuffs(player);
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        damageHealthBoard.removeScoreboard(player);
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
