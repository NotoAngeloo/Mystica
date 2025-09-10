package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.MysticalCrystal;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.Items.UnidentifiedItem;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StarterKit implements CommandExecutor {

    private final ProfileManager profileManager;

    public StarterKit(Mystica main){
        profileManager = main.getProfileManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        UnidentifiedItem weapon = new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 1);
        UnidentifiedItem helmet = new UnidentifiedItem(EquipmentSlot.HEAD, 1, 1);
        UnidentifiedItem chest = new UnidentifiedItem(EquipmentSlot.CHEST, 1, 1);
        UnidentifiedItem legs = new UnidentifiedItem(EquipmentSlot.LEGS, 1, 1);
        UnidentifiedItem boots = new UnidentifiedItem(EquipmentSlot.BOOTS, 1, 1);

        MysticalCrystal crystal = new MysticalCrystal(1);

        if(args.length == 0){

            if(!(sender instanceof Player player)){
                sender.sendMessage("only players");
                return true;
            }

            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(weapon);
            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(helmet);
            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(chest);
            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(legs);
            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(boots);

            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(crystal);

            return true;
        }

        if(args.length == 1){

            Player player = Bukkit.getPlayer(args[0]);

            if(player == null){
                sender.sendMessage("player doesn't exist");
                return true;
            }

            if(!player.isOnline()){
                sender.sendMessage("player not online");
                return true;
            }

            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(weapon);
            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(helmet);
            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(chest);
            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(legs);
            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(boots);


            return true;
        }


        return true;
    }
}
