package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WhatAreMyStats implements CommandExecutor {

    private final ProfileManager profileManager;

    public WhatAreMyStats(Mystica main){
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        int level = profileManager.getAnyProfile(player).getStats().getLevel();
        int attack = profileManager.getAnyProfile(player).getTotalAttack();
        int health = profileManager.getAnyProfile(player).getTotalHealth();
        int mana = profileManager.getAnyProfile(player).getTotalMana();
        int defense = profileManager.getAnyProfile(player).getTotalDefense();
        int magic_defense = profileManager.getAnyProfile(player).getTotalMagicDefense();
        int crit = profileManager.getAnyProfile(player).getTotalCrit();

        player.sendMessage("level: " + level);
        player.sendMessage("attack: " + attack);
        player.sendMessage("health: " + health);
        player.sendMessage("mana: " + mana);
        player.sendMessage("defense: " + defense);
        player.sendMessage("magic_defense: " + magic_defense);
        player.sendMessage("crit: " + crit);

        return true;
    }
}
