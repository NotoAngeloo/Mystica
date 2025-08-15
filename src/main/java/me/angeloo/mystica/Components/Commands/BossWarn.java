package me.angeloo.mystica.Components.Commands;

import io.lumine.mythic.bukkit.entities.BukkitIllusioner;
import me.angeloo.mystica.CustomEvents.HudUpdateEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.BarType;
import me.angeloo.mystica.Utility.Hud.BossWarnings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BossWarn implements CommandExecutor {

    private final Mystica main;
    private final BossWarnings bossWarnings;

    public BossWarn(Mystica main){
        this.main = main;
        bossWarnings = main.getBossWarnings();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


        if (!(sender.isOp())) {
            sender.sendMessage("you do not have permissions");
            return true;
        }

        //player, warning, time

        if (args.length >= 3) {


            if (args[0].equalsIgnoreCase("<target.uuid>")) {
                return true;
            }


            Player player = Bukkit.getPlayer(UUID.fromString(args[0]));

            //Bukkit.getLogger().info(args[0]);

            //Bukkit.getLogger().info(String.valueOf(player));

            if(player == null){
                Bukkit.getLogger().info("player " + args[0] + " not found");
                return true;
            }

            //Bukkit.getLogger().info("5");

            int time;

            try {
                time = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e){
                Bukkit.getLogger().info("bosswarn time input not number");
                return true;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]);
                if (i < args.length - 1) {
                    sb.append(" ");
                }
            }
            String warning = sb.toString();

            bossWarnings.setWarning(player, warning, time);

            new BukkitRunnable(){
                int left = time;
                @Override
                public void run(){
                    Bukkit.getPluginManager().callEvent(new HudUpdateEvent(player, BarType.Cast));

                    if(left <= 0){
                        this.cancel();
                    }

                    left--;

                }
            }.runTaskTimer(main, 0, 1);

            return true;
        }


        return true;
    }

}
