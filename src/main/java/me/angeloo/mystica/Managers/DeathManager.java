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
        //abilityManager.setSkillRunning(player, false);
        abilityManager.interruptBasic(player);
        dpsManager.removeDps(player);

        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        Bukkit.getServer().getPluginManager().callEvent(new TargetBarShouldUpdateEvent(player));
    }

    public void playerNowLive(Player player, Boolean bySkill, Player playerWhoCastSkill){

        profileManager.getAnyProfile(player).setIfDead(false);
        player.setGlowing(false);
        player.setInvisible(false);
        player.setFireTicks(0);
        player.setVisualFire(false);
        //more effects?

        player.getInventory().clear();

        profileManager.getAnyProfile(player).setIfInCombat(false);

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

        if(!bySkill){
            player.teleport(player.getWorld().getSpawnLocation());
        }
        else{
            player.teleport(playerWhoCastSkill.getLocation());
            double halfHealth = ((double) profileManager.getAnyProfile(player).getTotalHealth() / 2);

            changeResourceHandler.subtractHealthFromEntity(player, halfHealth, playerWhoCastSkill);
        }


        Scoreboard scoreboard = player.getScoreboard();
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        Bukkit.getServer().getPluginManager().callEvent(new StatusUpdateEvent(player));
        Bukkit.getServer().getPluginManager().callEvent(new TargetBarShouldUpdateEvent(player));
    }

}
