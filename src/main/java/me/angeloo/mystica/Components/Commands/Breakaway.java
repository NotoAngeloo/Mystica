package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Breakaway implements CommandExecutor {

    private final ProfileManager profileManager;

    private final Map<UUID, Long> cooldown = new HashMap<>();

    public Breakaway(Mystica main){
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage("only players");
            return true;
        }

        Player player = (Player) sender;

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            sender.sendMessage("cannot use in combat");
            return true;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            sender.sendMessage("cannot use while dead");
            return true;
        }

        if(cooldown.get(player.getUniqueId()) == null){
            cooldown.put(player.getUniqueId(), (System.currentTimeMillis() / 1000) - 300);
        }

        long currentTime = System.currentTimeMillis() / 1000;
        if(currentTime - cooldown.get(player.getUniqueId()) < 300){
            sender.sendMessage("command on cooldown");
            return true;
        }
        cooldown.put(player.getUniqueId(), currentTime);

        player.teleport(player.getWorld().getSpawnLocation());

        return true;
    }
}
