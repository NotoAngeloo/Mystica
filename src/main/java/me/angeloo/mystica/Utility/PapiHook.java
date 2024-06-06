package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PapiHook extends PlaceholderExpansion {

    private final Mystica main;
    private final RandomValidEntity randomValidEntity;
    private final ProfileManager profileManager;

    public PapiHook(Mystica main){
        this.main = main;
        randomValidEntity = new RandomValidEntity(main);
        profileManager = main.getProfileManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Mystica";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mystica";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params){

        if(offlinePlayer != null && offlinePlayer.isOnline()){
            Player player = offlinePlayer.getPlayer();

            switch (params.toLowerCase()){
                case "bosstarget":{
                    assert player != null;
                    return String.valueOf(profileManager.getBossTarget(player));
                }
                case "random":{

                    assert player != null;

                    UUID entityId = randomValidEntity.getRandomEntity(player);

                    if(entityId == null){
                        return "none";
                    }

                    LivingEntity targetedEntity = (LivingEntity) Bukkit.getEntity(entityId);

                    if(profileManager.getAnyProfile(targetedEntity).getIfDead()){
                        return "none";
                    }


                    return String.valueOf(randomValidEntity.getRandomEntity(player));
                }
                case "class":{
                    return profileManager.getAnyProfile(player).getPlayerClass();
                }
                default:{
                    return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone(params));
                }
            }


        }

        return null;
    }

    public static void registerHook(Mystica main){
        new PapiHook(main).register();
    }
}
