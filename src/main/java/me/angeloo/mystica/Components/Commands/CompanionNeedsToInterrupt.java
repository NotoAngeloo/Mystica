package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.Parties.FakePlayerAiManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CompanionNeedsToInterrupt implements CommandExecutor {

    private final FakePlayerAiManager fakePlayerAiManager;

    public CompanionNeedsToInterrupt(Mystica main){
        fakePlayerAiManager = main.getFakePlayerAiManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }


        if(args.length==2){

            UUID uuid = UUID.fromString(args[0]);
            Entity companion = Bukkit.getEntity(uuid);

            boolean needs = Boolean.parseBoolean(args[1]);

            if(companion instanceof LivingEntity){
                fakePlayerAiManager.setNeedToInterrupt((LivingEntity) companion, needs);
            }


        }


        return true;
    }
}
