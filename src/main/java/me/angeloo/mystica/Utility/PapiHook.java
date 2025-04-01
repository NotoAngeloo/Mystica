package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PapiHook extends PlaceholderExpansion {

    private final Mystica main;
    private final MysticaEntityGrabber mysticaEntityGrabber;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;

    public PapiHook(Mystica main){
        this.main = main;
        mysticaEntityGrabber = new MysticaEntityGrabber(main);
        profileManager = main.getProfileManager();
        targetManager = main.getTargetManager();
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
                case "random":{
                    assert player != null;
                    return String.valueOf(mysticaEntityGrabber.getRandomEntity(player));
                }
                case "lowestphp":{
                    assert player != null;
                    return String.valueOf(mysticaEntityGrabber.getLowestPhp(player));
                }
                case "class":{
                    return profileManager.getAnyProfile(player).getPlayerClass();
                }
                case "amount":{
                    assert player != null;
                    return String.valueOf(mysticaEntityGrabber.getValidAmount(player));
                }
                case "bosstarget":{
                    assert player != null;
                    return String.valueOf(mysticaEntityGrabber.getBossTarget(player));
                }
                case "leader":{
                    assert player != null;
                    return String.valueOf(mysticaEntityGrabber.getMPartyLeader(player));
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
