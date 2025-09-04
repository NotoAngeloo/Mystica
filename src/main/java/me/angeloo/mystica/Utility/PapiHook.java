package me.angeloo.mystica.Utility;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Managers.TargetManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Logic.DamageBoardPlaceholders;
import me.angeloo.mystica.Utility.Logic.MysticaEntityGrabber;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiHook extends PlaceholderExpansion {

    private final MysticaEntityGrabber mysticaEntityGrabber;
    private final DamageBoardPlaceholders damageBoardPlaceholders;
    private final ProfileManager profileManager;
    private final TargetManager targetManager;

    public PapiHook(Mystica main){
        mysticaEntityGrabber = new MysticaEntityGrabber(main);
        damageBoardPlaceholders = main.getHudManager().getDamageBoardPlaceholders();
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
            assert player != null;

            switch (params.toLowerCase()) {
                case "random" -> {
                    return String.valueOf(mysticaEntityGrabber.getRandomEntity(player));
                }
                case "lowestphp" -> {
                    return String.valueOf(mysticaEntityGrabber.getLowestPhp(player));
                }
                case "class" -> {
                    return profileManager.getAnyProfile(player).getPlayerClass().toString();
                }
                case "amount" -> {
                    return String.valueOf(mysticaEntityGrabber.getValidAmount(player));
                }
                case "bosstarget" -> {
                    return String.valueOf(mysticaEntityGrabber.getBossTarget(player));
                }
                case "leader" -> {
                    return String.valueOf(mysticaEntityGrabber.getMPartyLeader(player));
                }
                case "damage_bar_1" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamage_Bar_1(player));
                }
                case "damage_player_1" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamagePlayer_1(player));
                }
                case "damage_bar_2" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamage_Bar_2(player));
                }
                case "damage_player_2" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamagePlayer_2(player));
                }
                case "damage_bar_3" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamage_Bar_3(player));
                }
                case "damage_player_3" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamagePlayer_3(player));
                }
                case "damage_bar_4" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamage_Bar_4(player));
                }
                case "damage_player_4" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamagePlayer_4(player));
                }
                case "damage_bar_5" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamage_Bar_5(player));
                }
                case "damage_player_5" -> {
                    return String.valueOf(damageBoardPlaceholders.getDamagePlayer_5(player));
                }
                case "dps_1" -> {
                    return String.valueOf(damageBoardPlaceholders.getDps_1(player));
                }
                case "dps_2" -> {
                    return String.valueOf(damageBoardPlaceholders.getDps_2(player));
                }
                case "dps_3" -> {
                    return String.valueOf(damageBoardPlaceholders.getDps_3(player));
                }
                case "dps_4" -> {
                    return String.valueOf(damageBoardPlaceholders.getDps_4(player));
                }
                case "dps_5" -> {
                    return String.valueOf(damageBoardPlaceholders.getDps_5(player));
                }
                case "combat" -> {
                    return String.valueOf(profileManager.getAnyProfile(player).getIfInCombat());
                }
                case "dead" ->{
                    return String.valueOf(profileManager.getAnyProfile(player).getIfDead());
                }

            }


        }

        return null;
    }

    public static void registerHook(Mystica main){
        new PapiHook(main).register();
    }
}
