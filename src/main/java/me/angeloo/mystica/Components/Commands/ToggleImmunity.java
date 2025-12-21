package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.DamageModifiers.Immune;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleImmunity implements CommandExecutor {

    private final StatusEffectManager statusEffectManager;

    public ToggleImmunity(Mystica main){
        statusEffectManager = main.getStatusEffectManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(sender instanceof Player player)){
            sender.sendMessage("only players");
            return true;
        }

        if(statusEffectManager.hasEffect(player, "immune")){
            statusEffectManager.removeEffect(player, "immune");
            player.sendMessage("removed immunity");
            return true;
        }

        statusEffectManager.applyEffect(player, new Immune(), -1, null);

        player.sendMessage("applied immunity");

        return true;
    }
}
