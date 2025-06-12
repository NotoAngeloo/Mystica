package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Managers.AggroManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Hidden {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final AggroManager aggroManager;

    public Hidden(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        aggroManager = main.getAggroManager();
    }

    public void hidePlayer(LivingEntity caster, boolean blacklist){

        caster.setInvisible(true);

        if(caster instanceof Player){
            Player player = (Player) caster;
            player.getInventory().setItemInMainHand(null);
            player.getInventory().setItemInOffHand(null);
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }


        if(blacklist){

            aggroManager.removeHighPriorityTarget(caster.getUniqueId());
            aggroManager.addToBlackList(caster);
            aggroManager.removeFromAllAttackerLists(caster);
        }


    }

    public void unhidePlayer(LivingEntity caster){

        boolean combatStatus = true;

        if(caster instanceof Player){
            combatStatus = profileManager.getAnyProfile(caster).getIfInCombat();
        }

        boolean deathStatus = profileManager.getAnyProfile(caster).getIfDead();

        if(caster instanceof Player){
            Player player = (Player) caster;
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status"));
        }


        if(!deathStatus){
            caster.setInvisible(false);

            if(!combatStatus){
                return;
            }

            if(caster instanceof Player){

                showWeapons((Player) caster);

                DisplayWeapons displayWeapons  = new DisplayWeapons(main);
                displayWeapons.displayArmor((Player) caster);
            }


            aggroManager.removeFromBlackList(caster);
        }

    }

    public void showWeapons(Player player){

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(playerEquipment.getWeapon() != null){
            player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
            ItemStack offhand = playerEquipment.getWeapon().clone();
            ItemMeta offhandItemMeta = offhand.getItemMeta();
            offhandItemMeta.setCustomModelData(offhand.getItemMeta().getCustomModelData() + 1);
            offhand.setItemMeta(offhandItemMeta);
            player.getInventory().setItemInOffHand(offhand);

        }

    }

}


