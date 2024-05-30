package me.angeloo.mystica.Components.BuffsAndDebuffs;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.Managers.AggroManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
            ((Player)caster).getInventory().setItemInMainHand(null);
            ((Player)caster).getInventory().setItemInOffHand(null);
            ((Player)caster).getInventory().setHelmet(null);
            ((Player)caster).getInventory().setChestplate(null);
            ((Player)caster).getInventory().setLeggings(null);
            ((Player)caster).getInventory().setBoots(null);
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
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
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent((Player) caster));
        }


        if(!deathStatus){
            caster.setInvisible(false);

            if(!combatStatus){
                return;
            }

            if(caster instanceof Player){
                PlayerEquipment playerEquipment = profileManager.getAnyProfile(caster).getPlayerEquipment();

                if(playerEquipment.getWeapon() != null){
                    ((Player)caster).getInventory().setItemInMainHand(playerEquipment.getWeapon());
                }

                if (playerEquipment.getOffhand() != null){
                    ((Player)caster).getInventory().setItemInOffHand(playerEquipment.getOffhand());
                }

                DisplayWeapons displayWeapons  = new DisplayWeapons(main);
                displayWeapons.displayArmor((Player) caster);
            }


            aggroManager.removeFromBlackList(caster);
        }

    }

    public void showWeapons(Player player){
        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(playerEquipment.getWeapon() != null){
            player.getInventory().setItemInMainHand(playerEquipment.getWeapon());
        }

        if (playerEquipment.getOffhand() != null){
            player.getInventory().setItemInOffHand(playerEquipment.getOffhand());
        }
    }

}


