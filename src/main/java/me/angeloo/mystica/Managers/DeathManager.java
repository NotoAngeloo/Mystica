package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.CustomEvents.AiSignalEvent;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.CustomEvents.MysticaPlayerDeathEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.Enums.BarType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;


public class DeathManager {

    private final ProfileManager profileManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AbilityManager abilityManager;
    private final AggroManager aggroManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final GravestoneManager gravestoneManager;

    public DeathManager(Mystica main){
        profileManager = main.getProfileManager();
        changeResourceHandler = main.getChangeResourceHandler();
        abilityManager = main.getAbilityManager();
        aggroManager = main.getAggroManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        gravestoneManager = main.getGravestoneManager();
    }


    public void playerNowDead(Player player){

        Location deathLoc = player.getLocation();

        Profile playerProfile = profileManager.getAnyProfile(player);



        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);

        profileManager.getAnyProfile(player).setIfDead(true);

        //aggroManager.removeFromAllAttackerLists(player);

        player.sendMessage("You have died");

        player.setHealth(20);
        playerProfile.setCurrentHealth(playerProfile.getTotalHealth());

        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setInvisible(true);
        //player.setGlowing(true);

        Entity gravestone;

        try{
            gravestone = MythicBukkit.inst().getAPIHelper().spawnMythicMob("Corpse", deathLoc);
            gravestone.setCustomName(player.getName());
            gravestoneManager.placeGravestone(gravestone, player);
        }
        catch (InvalidMobTypeException e){
            throw new RuntimeException(e);
        }

        List<Entity> passengers = player.getPassengers();

        for(Entity passenger : passengers){

            if(MythicBukkit.inst().getAPIHelper().isMythicMob(passenger.getUniqueId())){
                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(passenger).getEntity();
                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(passenger).signalMob(abstractEntity, "playerdeath");
            }
        }


        abilityManager.resetAbilityBuffs(player);
        buffAndDebuffManager.removeAllBuffsAndDebuffs(player);
        abilityManager.interruptBasic(player);
        //dpsManager.removeDps(player);
        Bukkit.getServer().getPluginManager().callEvent(new MysticaPlayerDeathEvent(player));
    }

    public void playerNowLive(LivingEntity target, Boolean bySkill, LivingEntity entityWhoCastSkill){

        gravestoneManager.removeGravestone(target);

        profileManager.getAnyProfile(target).setIfDead(false);
        target.setGlowing(false);
        target.setInvisible(false);
        target.setFireTicks(0);
        target.setVisualFire(false);
        //more effects?

        if(target instanceof Player){
            ((Player) target).setGameMode(GameMode.SURVIVAL);
            ((Player) target).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player) target, BarType.Resource, true));
            ((Player)target).getInventory().clear();
        }


        profileManager.getAnyProfile(target).setIfInCombat(false);



        if(!bySkill){
            target.teleport(target.getWorld().getSpawnLocation());

            abilityManager.resetCooldowns(target);

            if(target instanceof Player){

                if(!profileManager.getCompanions((Player) target).isEmpty()){
                    for(UUID companion : profileManager.getCompanions((Player) target)){

                        LivingEntity livingEntity = (LivingEntity) Bukkit.getEntity(companion);

                        if(livingEntity != null){
                            livingEntity.teleport(target.getWorld().getSpawnLocation());
                            playerNowLive(livingEntity, false, null);
                            Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(livingEntity, "reset"));
                            Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent((Player) target, BarType.Team, true));
                        }


                    }
                }
            }

        }
        else{
            target.teleport(entityWhoCastSkill.getLocation());
            double halfHealth = ((double) profileManager.getAnyProfile(target).getTotalHealth() / 2);

            changeResourceHandler.subtractHealthFromEntity(target, halfHealth, null, false);
        }

        if(!(target instanceof Player)){
            target.setAI(true);
        }




    }



}
