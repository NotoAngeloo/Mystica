package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Managers.InventoryIndexingManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ClassSetter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ClassSelectInventory implements Listener {

    private final Mystica main;
    private final InventoryIndexingManager inventoryIndexingManager;
    private final ClassSetter classSetter;

    public ClassSelectInventory(Mystica main){
        this.main = main;
        inventoryIndexingManager = main.getInventoryIndexingManager();
        classSetter = main.getClassSetter();
    }

    public Inventory openClassSelect(int index){

        //inv.setItem(22, getItem(Material.LIME_DYE, 0,"Select"));

        //inv.setItem(15, getItem(Material.ARROW,0,"Next"));

        //inv.setItem(11, getItem(Material.ARROW, 0, "Previous"));

        String title = "something went wrong";

        switch (index){
            case 0:{
                title = "\uF80A\uF801\uE06F\uF82A\uF821" + "\uF80A\uF802\uE070\uF82A\uF822";
                //inv.setItem(13, getAssassinItem());
                break;
            }
            case 1:{
                title = "elementalist";
                //inv.setItem(13, getElementalistItem());
                break;
            }
            case 2:{
                title = "mystic";
                //inv.setItem(13, getMysticItem());
                break;
            }
            case 3:{
                title = "paladin";
                //inv.setItem(13, getPaladinItem());
                break;
            }
            case 4:{
                title = "ranger";
                //inv.setItem(13, getRangerItem());
                break;
            }
            case 5:{
                title = "shadow knight";
                //inv.setItem(13, getShadowKnightItem());
                break;
            }
            case 6:{
                title = "warrior";
                //inv.setItem(13, getWarriorItem());
                break;
            }

        }


        //arrows
        //inv.setItem(45, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(53, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        //select
        //inv.setItem(48, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(49, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(50, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));



        return Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + title);
    }

    @EventHandler
    public void classClicks(InventoryClickEvent event){

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF80A\uF801\uE06F\uF82A\uF821" + "\uF80A\uF802\uE070\uF82A\uF822")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "elementalist")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "mystic")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "paladin")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "ranger")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "shadow knight")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "warrior")){

            event.setCancelled(true);

            if(event.getClickedInventory() == null){
                return;
            }

            Inventory inv = event.getView().getTopInventory();

            if(event.getClickedInventory() != inv){
                return;
            }

            String title = event.getView().getTitle();

            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();

            List<Integer> selectSlots = new ArrayList<>();
            selectSlots.add(48);
            selectSlots.add(49);
            selectSlots.add(50);

            if(selectSlots.contains(slot)){

                String colorlessTitle = title.replaceAll("§.", "");

                switch (colorlessTitle){
                    case ("\uF80A\uF801\uE06F\uF82A\uF821\uF80A\uF802\uE070\uF82A\uF822"):
                    {
                        classSetter.setClass(player, "Assassin");
                        player.closeInventory();
                        return;
                    }
                    case ("elementalist"):
                    {
                        classSetter.setClass(player, "Elementalist");
                        player.closeInventory();
                        return;
                    }
                    case ("mystic"):
                    {
                        classSetter.setClass(player, "Mystic");
                        player.closeInventory();
                        return;
                    }
                    case ("paladin"):
                    {
                        classSetter.setClass(player, "Paladin");
                        player.closeInventory();
                        return;
                    }
                    case ("ranger"):
                    {
                        classSetter.setClass(player, "Ranger");
                        player.closeInventory();
                        return;
                    }
                    case ("shadow knight"):
                    {
                        classSetter.setClass(player, "Shadow Knight");
                        player.closeInventory();
                        return;
                    }
                    case ("warrior"):
                    {
                        classSetter.setClass(player, "warrior");
                        player.closeInventory();
                        return;
                    }
                }

                Bukkit.getLogger().info("something went wrong");
                return;
            }



            int index = inventoryIndexingManager.getClassIndex(player);

            if(slot == 45){
                index++;
            }

            if(slot == 53){
                index--;
            }

            if(index<0){
                index = 6;
            }

            if(index>6){
                index = 0;
            }

            inventoryIndexingManager.setClassIndex(player, index);

            player.openInventory(new ClassSelectInventory(main).openClassSelect(index));
        }

    }


}
