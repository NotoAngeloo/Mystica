package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageHealthBoard;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.StatusDisplayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


import java.util.*;
import java.util.List;

public class CombatManager {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final DpsManager dpsManager;
    private final StatusDisplayer statusDisplayer;
    private final DamageHealthBoard damageHealthBoard;

    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        dpsManager = main.getDpsManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        statusDisplayer = new StatusDisplayer(main, manager);
        damageHealthBoard = main.getDamageHealthBoard();
    }


    public void startCombatTimer(Player player){

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            player.sendMessage("You are now in combat");

            profileManager.getAnyProfile(player).setSavedInv(player.getInventory().getContents());
            player.getInventory().clear();

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(playerEquipment.getWeapon() != null){
                player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
            }

            if (playerEquipment.getOffhand() != null){
                player.getInventory().setItemInOffHand(playerEquipment.getOffhand());
            }

            DisplayWeapons displayWeapons = new DisplayWeapons(main);
            displayWeapons.displayArmor(player);


            player.getInventory().setItem(13, getItem(new ItemStack(Material.BARRIER), "Exit Combat"));
            cooldownDisplayer.initializeItems(player);
        }

        profileManager.getAnyProfile(player).setIfInCombat(true);
        lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        statusDisplayer.displayStatus(player);
    }


    private long getLastCalledCombat(Player player){

        if(!lastCalledCombat.containsKey(player.getUniqueId())){
            lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastCalledCombat.get(player.getUniqueId());
    }


    public void tryToEndCombat(Player player){

        long currentTime = System.currentTimeMillis();
        long lastCalled = getLastCalledCombat(player);

        if(currentTime - lastCalled > 10000){
            forceCombatEnd(player);
        }
        else{
            player.sendMessage(((10000 -(currentTime - lastCalled))/1000) + " seconds left");
        }

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
        statusDisplayer.clearPlayerStatus(player);
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
