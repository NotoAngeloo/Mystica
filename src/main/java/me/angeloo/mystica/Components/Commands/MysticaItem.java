package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.Items.MysticaEquipment;
import me.angeloo.mystica.Components.Items.SoulStone;
import me.angeloo.mystica.Components.Items.UnidentifiedItem;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.EquipmentSlot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MysticaItem implements CommandExecutor {

    private final ProfileManager profileManager;
    private final InventoryItemGetter inventoryItemGetter;

    public MysticaItem(Mystica main){
        profileManager = main.getProfileManager();
        inventoryItemGetter = main.getItemGetter();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;


            /*MysticaEquipment equipmentT1 = new MysticaEquipment(EquipmentSlot.WEAPON, profileManager.getAnyProfile(player).getPlayerClass(), 1);
            MysticaEquipment equipmentT2 = new MysticaEquipment(EquipmentSlot.WEAPON, profileManager.getAnyProfile(player).getPlayerClass(), 1,
                    MysticaEquipment.StatType.Attack, MysticaEquipment.StatType.Defense);
            MysticaEquipment equipmentT3 = new MysticaEquipment(EquipmentSlot.WEAPON, profileManager.getAnyProfile(player).getPlayerClass(), 1,
                    MysticaEquipment.StatType.Attack, MysticaEquipment.StatType.Defense,
                    Pair.of(1,1),
                    Pair.of(2,2)
            );

            player.getInventory().addItem(equipmentT1.build());
            player.getInventory().addItem(equipmentT2.build());
            player.getInventory().addItem(equipmentT3.build());*/


            /*player.getInventory().addItem(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 1).build());
            player.getInventory().addItem(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 2).build());
            player.getInventory().addItem(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 3).build());*/

            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 1));

            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new MysticaEquipment(EquipmentSlot.WEAPON,
                    //profileManager.getAnyProfile(player).getPlayerClass(), 1));



            profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new SoulStone(66));

            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 1));
            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 2));
            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 3));

            //profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(new MysticaEquipment(EquipmentSlot.WEAPON, profileManager.getAnyProfile(player).getPlayerClass(), 2));

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


            return true;
        }


        return true;
    }

}
