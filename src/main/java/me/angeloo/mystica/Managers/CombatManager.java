package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.SetMenuItemsEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.CombatTick;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;

public class CombatManager {

    private final Mystica main;

    private final DisplayWeapons displayWeapons;
    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final AbilityManager abilityManager;
    private final CooldownDisplayer cooldownDisplayer;
    private final CombatTick combatTick;

    private final Map<UUID, Boolean> sheathed = new HashMap<>();
    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        displayWeapons = main.getDisplayWeapons();
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = manager;
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combatTick = new CombatTick(main, this, manager);
    }


    public void startCombatTimer(LivingEntity caster){

        if(profileManager.getAnyProfile(caster).fakePlayer()){
            profileManager.getAnyProfile(caster).setIfInCombat(true);
        }

        if(!(caster instanceof Player player)){
            return;
        }

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            //player.sendMessage("You are now in combat");

            player.closeInventory();
            player.getInventory().clear();
            displayWeapons.displayArmor(player);

            cooldownDisplayer.initializeItems(player);

            unSheathWeapon(player);

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

            boolean partyMemberDeathStatus = profileManager.getAnyProfile(member).getIfDead();

            if(partyMemberDeathStatus){
                continue;
            }


            if(member instanceof Player mPlayer){

                if(combatTimeEnough(mPlayer)){
                    continue;
                }

            }

            boolean partyMemberCombatStatus = profileManager.getAnyProfile(member).getIfInCombat();


            if(partyMemberCombatStatus){
                return false;
            }


        }

        return combatTimeEnough(player);
    }

    private boolean combatTimeEnough(Player player){
        long currentTime = System.currentTimeMillis();
        long lastCalled = getLastCalledCombat(player);
        //Bukkit.getLogger().info(String.valueOf(currentTime - lastCalled));

        return currentTime - lastCalled > 5000;
    }

    public void forceCombatEnd(Player player){

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        profileManager.getAnyProfile(player).setIfInCombat(false);

        if(profileManager.getAnyProfile(player).getPlayerClass().equals(PlayerClass.Paladin)){
            abilityManager.getPaladinAbilities().getPurity().reset(player);
        }


        //player.sendMessage("You are no longer in combat");

        //and restore their inventories


        //dpsManager.removeDps(player);
        abilityManager.resetAbilityBuffs(player);
        abilityManager.resetCooldowns(player);


        if(!profileManager.getAnyProfile(player).getIfDead()){
            player.setInvisible(false);
            sheathWeapon(player);
            Bukkit.getServer().getPluginManager().callEvent(new SetMenuItemsEvent(player));
        }
        Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Status));
    }

    public void unSheathWeapon(LivingEntity caster){

        if(!(caster instanceof Player player)){
            return;
        }

        //check if shealthed already
        if(!ifSheathed(player)){
            return;
        }

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

        sheathed.put(player.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){


                Player refreshedPlayer = Bukkit.getOfflinePlayer(player.getUniqueId()).getPlayer();

                if(refreshedPlayer == null){
                    return;
                }

                if(!refreshedPlayer.isOnline()){
                    return;
                }



                sheathWeapon(player);

            }
        }.runTaskLaterAsynchronously(main, 200);

    }

    public void sheathWeapon(Player player){

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        displayWeapons.displayArmor(player);
        player.getInventory().setItemInMainHand(null);
        sheathed.remove(player.getUniqueId());
        abilityManager.interruptBasic(player);
    }

    private boolean ifSheathed(Player player){
        return sheathed.getOrDefault(player.getUniqueId(), true);
    }

}
