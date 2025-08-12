package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.Parties.FakePlayerAiManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SetCaution implements CommandExecutor {

    private final ProfileManager profileManager;
    private final FakePlayerAiManager fakePlayerAiManager;

    public SetCaution(Mystica main){
        profileManager = main.getProfileManager();
        fakePlayerAiManager = main.getFakePlayerAiManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){

        if(!sender.isOp()){
            sender.sendMessage("you do not have permissions");
            return true;
        }


        if(args.length == 2){

            Entity entity = Bukkit.getEntity(UUID.fromString(args[0]));

            if(!(entity instanceof LivingEntity)){
                return true;
            }

            if(!profileManager.getAnyProfile((LivingEntity) entity).fakePlayer()){
                return true;
            }

            boolean setTo = Boolean.parseBoolean(args[1]);

            fakePlayerAiManager.setCaution((LivingEntity) entity, setTo);

            return true;
        }

        return true;
    }
}
