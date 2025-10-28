package me.angeloo.mystica.Components.Commands;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.CustomEvents.AiSignalEvent;
import me.angeloo.mystica.Components.Parties.FakePlayerAiManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StopCompanionRotation implements CommandExecutor {

    private final FakePlayerAiManager fakePlayerAiManager;

    public StopCompanionRotation(Mystica main){

        fakePlayerAiManager = main.getFakePlayerAiManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args){


        //player, signal
        if(args.length == 1){

            LivingEntity target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));

            if (target == null) {
                sender.sendMessage("target null");
                return true;
            }

            if(MythicBukkit.inst().getAPIHelper().isMythicMob(target.getUniqueId())){
                fakePlayerAiManager.stopAiTask(target.getUniqueId());
                Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(target, "stop"));
            }


        }

        if(args.length == 2){

            LivingEntity target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));

            if (target == null) {
                sender.sendMessage("target null");
                return true;
            }

            if(MythicBukkit.inst().getAPIHelper().isMythicMob(target.getUniqueId())){
                fakePlayerAiManager.stopAiTask(target.getUniqueId());
                Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(target, "bossdeath"));
            }

        }

        return true;
    }

}
