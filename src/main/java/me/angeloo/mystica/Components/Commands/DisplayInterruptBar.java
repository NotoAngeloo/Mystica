package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Managers.BossCastingManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DisplayInterruptBar implements CommandExecutor {

    private final BossCastingManager bossCastingManager;

    public DisplayInterruptBar(Mystica main){
        bossCastingManager = main.getBossCastingManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        //uuid, speed, max

        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        if (args.length == 3) {

            if (args[0].equalsIgnoreCase("<caster.uuid>")) {
                return true;
            }

            LivingEntity caster = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));

            double speed = Double.parseDouble(args[1]);
            double max = Double.parseDouble(args[2]);

            assert caster != null;
            bossCastingManager.startCastBar(caster, speed, max);

            return true;
        }


        return true;
    }
}
