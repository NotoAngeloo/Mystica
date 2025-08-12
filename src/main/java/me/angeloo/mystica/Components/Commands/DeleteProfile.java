package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteProfile implements CommandExecutor {

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;

    public DeleteProfile(Mystica main){
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(!sender.isOp()){
            return true;
        }

        if(args.length != 1){

            sender.sendMessage("improper usage");

            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null){
            sender.sendMessage("player doesn't exist");
            return true;
        }

        //mysticaPartyManager.removeFromMysticaPartyMap(player);
        abilityManager.resetAbilityBuffs(player);
        abilityManager.resetCooldowns(player);
        buffAndDebuffManager.removeAllBuffsAndDebuffs(player);
        profileManager.removePlayerProfile(player);

        return true;
    }
}
