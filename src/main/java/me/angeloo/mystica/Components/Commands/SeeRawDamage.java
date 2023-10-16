package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageCalculator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SeeRawDamage implements CommandExecutor {

    private final DamageCalculator damageCalculator;

    public SeeRawDamage(Mystica main){
        damageCalculator = main.getDamageCalculator();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        damageCalculator.toggleSeeingRawDamage(player);

        return true;
    }
}
