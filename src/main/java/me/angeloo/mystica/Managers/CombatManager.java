package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.CombatTick;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.*;

public class CombatManager {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final ItemManager itemManager;
    private final AbilityManager abilityManager;
    private final DpsManager dpsManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final CombatTick combatTick;

    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = manager;
        itemManager = main.getItemManager();
        dpsManager = main.getDpsManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combatTick = new CombatTick(main, this, manager);
    }


    public void startCombatTimer(LivingEntity caster){

        if(!(caster instanceof Player)){
            return;
        }

        Player player = (Player) caster;

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            //player.sendMessage("You are now in combat");

            profileManager.getAnyProfile(player).setSavedInv(player.getInventory().getContents());
            player.getInventory().clear();

            DisplayWeapons displayWeapons = new DisplayWeapons(main);
            displayWeapons.displayArmor(player);

            player.getInventory().setHeldItemSlot(8);

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(playerEquipment.getWeapon() != null){
                ItemStack weapon = playerEquipment.getWeapon();
                ItemStack offhand = weapon.clone();
                ItemMeta offhandMeta = offhand.getItemMeta();
                offhandMeta.setCustomModelData(weapon.getItemMeta().getCustomModelData() + 1);
                offhand.setItemMeta(offhandMeta);
                player.getInventory().setItemInMainHand(weapon);
                player.getInventory().setItemInOffHand(offhand);
            }
            else{
                player.getInventory().setItemInMainHand(itemManager.getNoneEquipment().getBaseWeapon());
            }


            cooldownDisplayer.initializeItems(player);

            /*PluginManager pluginManager = Bukkit.getPluginManager();
            Plugin interactions =  pluginManager.getPlugin("interactions");
            if (interactions != null && interactions.isEnabled()) {
                Server server = Bukkit.getServer();

                server.dispatchCommand(server.getConsoleSender(), "interactions stop " + player.getName());
            }*/


            combatTick.startCombatTickFor(player);
        }

        profileManager.getAnyProfile(player).setIfInCombat(true);
        lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
    }


    private long getLastCalledCombat(Player player){

        if(!lastCalledCombat.containsKey(player.getUniqueId())){
            lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastCalledCombat.get(player.getUniqueId());
    }

    public boolean canLeaveCombat(Player player){

        long currentTime = System.currentTimeMillis();
        long lastCalled = getLastCalledCombat(player);

        //Bukkit.getLogger().info(String.valueOf(currentTime - lastCalled));

        return currentTime - lastCalled > 10000;
    }

    public void forceCombatEnd(Player player){

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        profileManager.getAnyProfile(player).setIfInCombat(false);

        if(profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("paladin")){
            abilityManager.getPaladinAbilities().getPurity().resetPurity(player);
        }


        //player.sendMessage("You are no longer in combat");

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


        //dpsManager.removeDps(player);
        abilityManager.resetAbilityBuffs(player);
        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
    }




}
