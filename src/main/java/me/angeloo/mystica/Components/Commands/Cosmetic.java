package me.angeloo.mystica.Components.Commands;

import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class Cosmetic implements CommandExecutor {

    private final ProfileManager profileManager;
    private final DisplayWeapons displayWeapons;

    public Cosmetic(Mystica main){
        profileManager = main.getProfileManager();
        displayWeapons = new DisplayWeapons(main);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if(args.length == 0){

            if(!(sender instanceof Player)){
                sender.sendMessage("only players");
                return true;
            }

            Player player = (Player) sender;

            if(!profileManager.getAnyProfile(player).getPlayerClass().equalsIgnoreCase("paladin")){
                player.sendMessage("wrong class");
                return true;
            }

            if(!profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("divine")){
                player.sendMessage("wrong subclass");
                return true;
            }

            PlayerEquipment equipment = profileManager.getAnyProfile(player).getPlayerEquipment();

            ItemStack weapon = equipment.getWeapon();
            ItemMeta weaponMeta = weapon.getItemMeta();

            ItemStack helmet = equipment.getHelmet();
            ItemMeta helmetMeta = helmet.getItemMeta();

            ItemStack chestPlate = equipment.getChestPlate();
            ItemMeta chestMeta = chestPlate.getItemMeta();
            ItemStack leggings = equipment.getLeggings();
            ItemMeta leggingMeta = leggings.getItemMeta();
            ItemStack boots = equipment.getBoots();
            ItemMeta bootMeta = boots.getItemMeta();

            ItemStack newHelmet = new ItemStack(Material.IRON_NUGGET);
            helmetMeta.setDisplayName(ChatColor.of(new Color( 89, 147, 153)) + "Divine Helmet");


            weaponMeta.setDisplayName(ChatColor.of(new Color( 89, 147, 153)) + "Divine Sword");
            weaponMeta.setCustomModelData(4);


            chestMeta.setDisplayName(ChatColor.of(new Color( 89, 147, 153)) + "Divine Plate");
            leggingMeta.setDisplayName(ChatColor.of(new Color( 89, 147, 153)) + "Divine Breeches");
            bootMeta.setDisplayName(ChatColor.of(new Color( 89, 147, 153)) + "Divine Boots");

            newHelmet.setItemMeta(helmetMeta);

            weapon.setItemMeta(weaponMeta);

            chestPlate.setItemMeta(chestMeta);
            leggings.setItemMeta(leggingMeta);
            boots.setItemMeta(bootMeta);



            equipment.setWeapon(weapon);

            equipment.setHelmet(newHelmet);

            equipment.setChestPlate(chestPlate);
            equipment.setLeggings(leggings);
            equipment.setBoots(boots);

            displayWeapons.displayWeapons(player);
            displayWeapons.displayArmor(player);

            return true;
        }



        return true;
    }
}
