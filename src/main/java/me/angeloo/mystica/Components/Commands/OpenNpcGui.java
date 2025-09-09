package me.angeloo.mystica.Components.Commands;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Quests.Inventories.PickQuestInventory;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OpenNpcGui implements CommandExecutor {

    private final PickQuestInventory pickQuestInventory;

    public OpenNpcGui(Mystica main){
        pickQuestInventory = main.getPickQuestInventory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 2){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                return true;
            }

            UUID mobId = UUID.fromString(args[1]);

            if(!MythicBukkit.inst().getAPIHelper().isMythicMob(mobId)){
                return true;
            }

            Entity entity = Bukkit.getEntity(mobId);

            MythicMob mob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getType();

            //Bukkit.getLogger().info(String.valueOf(mob));

            pickQuestInventory.open(player, mob);
        }

        return true;
    }

}
