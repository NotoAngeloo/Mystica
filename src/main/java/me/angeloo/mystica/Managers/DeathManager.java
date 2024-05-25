package me.angeloo.mystica.Managers;

import me.angeloo.mystica.CustomEvents.StatusUpdateEvent;
import me.angeloo.mystica.CustomEvents.TargetBarShouldUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;


public class DeathManager {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final AggroManager aggroManager;
    private final DpsManager dpsManager;
    private final BuffAndDebuffManager buffAndDebuffManager;

    public DeathManager(Mystica main){
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = main.getAbilityManager();
        aggroManager = main.getAggroManager();
        dpsManager = main.getDpsManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    public void playerNowDead(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){

            profileManager.getAnyProfile(player).setSavedInv(player.getInventory().getContents());

        }

        player.getInventory().clear();

        profileManager.getAnyProfile(player).setIfDead(true);

        aggroManager.removeFromAllAttackerLists(player);

        player.sendMessage("You have died");

        player.setHealth(20);
        playerProfile.setCurrentHealth(playerProfile.getTotalHealth());
        playerProfile.setCurrentMana(playerProfile.getTotalMana());

        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setInvisible(true);
        player.setGlowing(true);
        abilityManager.resetAbilityBuffs(player);
        buffAndDebuffManager.removeAllBuffsAndDebuffs(player);
        abilityManager.interruptBasic(player);
        dpsManager.removeDps(player);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        Bukkit.getServer().getPluginManager().callEvent(new TargetBarShouldUpdateEvent(player));
    }

    public void playerNowLive(LivingEntity target, Boolean bySkill, LivingEntity entityWhoCastSkill){

        profileManager.getAnyProfile(target).setIfDead(false);
        target.setGlowing(false);
        target.setInvisible(false);
        target.setFireTicks(0);
        target.setVisualFire(false);
        //more effects?


        if(target instanceof Player){
            ((Player)target).getInventory().clear();
        }

        profileManager.getAnyProfile(target).setIfInCombat(false);

        if(target instanceof Player){
            ItemStack[] savedInv = profileManager.getAnyProfile(target).getSavedInv();
            boolean allNull = true;
            for(ItemStack item : savedInv){
                if(item != null){
                    allNull = false;
                    break;
                }
            }
            if(!allNull){
                ((Player)target).getInventory().setContents(savedInv);
                profileManager.getAnyProfile(target).removeSavedInv();
            }
        }


        if(!bySkill){
            //TODO: res all nearby fake players, if not by skill
            target.teleport(target.getWorld().getSpawnLocation());
            if(target instanceof Player){
                abilityManager.resetCooldowns((Player) target);
            }

        }
        else{
            target.teleport(entityWhoCastSkill.getLocation());
            double halfHealth = ((double) profileManager.getAnyProfile(target).getTotalHealth() / 2);

            changeResourceHandler.subtractHealthFromEntity(target, halfHealth, entityWhoCastSkill);
        }

        if(!(target instanceof Player)){
            target.setAI(true);
        }


        if(target instanceof Player){
            Scoreboard scoreboard = ((Player)target).getScoreboard();
            scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            ((Player)target).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(((Player)target)));
            Bukkit.getServer().getPluginManager().callEvent(new TargetBarShouldUpdateEvent(target));
        }


    }

}
