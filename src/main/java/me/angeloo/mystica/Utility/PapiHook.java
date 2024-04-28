package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiHook extends PlaceholderExpansion {

    private final ProfileManager profileManager;

    public PapiHook(Mystica main){
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
