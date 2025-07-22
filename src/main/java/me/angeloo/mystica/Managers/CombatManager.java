package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.CombatTick;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.*;

public class CombatManager {

    private final DisplayWeapons displayWeapons;
    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final CombatTick combatTick;

    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        displayWeapons = main.getDisplayWeapons();
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = manager;
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combatTick = new CombatTick(main, this, manager);
    }


    public void startCombatTimer(LivingEntity caster){

        if(!(caster instanceof Player player)){
            return;
        }

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            //player.sendMessage("You are now in combat");

            player.getInventory().clear();

            displayWeapons.displayArmor(player);

            player.getInventory().setHeldItemSlot(8);

            PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            if(playerEquipment.getWeapon() != null){
                ItemStack weapon = playerEquipment.getWeapon().build();
                ItemStack offhand = weapon.clone();
                ItemMeta offhandMeta = offhand.getItemMeta();
                assert offhandMeta != null;
                offhandMeta.setCustomModelData(offhand.getItemMeta().getCustomModelData() + 1);
                offhand.setItemMeta(offhandMeta);
                player.getInventory().setItemInMainHand(weapon);
                player.getInventory().setItemInOffHand(offhand);
            }
            else{
                //player.getInventory().setItemInMainHand(inventoryItemGetter.getNoneEquipment().getBaseWeapon());
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
        //Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
    }


    private long getLastCalledCombat(Player player){

        if(!lastCalledCombat.containsKey(player.getUniqueId())){
            lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastCalledCombat.get(player.getUniqueId());
    }

    public boolean canLeaveCombat(Player player){

        //first check mparty

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        for(LivingEntity member : mParty){

            if(member == player){
                continue;
            }

            if(member instanceof Player){
                boolean partyMemberDeathStatus = profileManager.getAnyProfile(member).getIfDead();

                if(partyMemberDeathStatus){
                    continue;
                }
            }


            boolean partyMemberCombatStatus = profileManager.getAnyProfile(member).getIfInCombat();

            if(partyMemberCombatStatus){
                return false;
            }


        }

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

        if(profileManager.getAnyProfile(player).getPlayerClass().equals(PlayerClass.Paladin)){
            abilityManager.getPaladinAbilities().getPurity().resetPurity(player);
        }


        //player.sendMessage("You are no longer in combat");

        //and restore their inventories

        if(!profileManager.getAnyProfile(player).getIfDead()){

            player.setInvisible(false);

            player.getInventory().clear();

        }


        //dpsManager.removeDps(player);
        abilityManager.resetAbilityBuffs(player);
        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", true));
    }




}
