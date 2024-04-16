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

            if(params.equalsIgnoreCase("class")){
                return profileManager.getAnyProfile(player).getPlayerClass();
            }

            if(params.equalsIgnoreCase("tutorial")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("tutorial"));
            }

            if(params.equalsIgnoreCase("lindwyrm_visit")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("lindwyrm_visit"));
            }

            if(params.equalsIgnoreCase("windbluff_visit")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("windbluff_visit"));
            }

            if(params.equalsIgnoreCase("tradecamp_visit")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("tradecamp_visit"));
            }

            if(params.equalsIgnoreCase("helping_hand_accept")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("helping_hand_accept"));
            }

            if(params.equalsIgnoreCase("sewer_accept")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("sewer_accept"));
            }

            if(params.equalsIgnoreCase("sewer2_accept")){
                return String.valueOf(profileManager.getAnyProfile(player).getMilestones().getMilestone("sewer2_accept"));
            }

        }

        return null;
    }

    public static void registerHook(Mystica main){
        new PapiHook(main).register();
    }
}
