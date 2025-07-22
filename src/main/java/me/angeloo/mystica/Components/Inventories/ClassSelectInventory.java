package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ClassSetter;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.List;

public class ClassSelectInventory implements Listener {

    private final Mystica main;
    private final CustomInventoryManager customInventoryManager;
    private final ClassSetter classSetter;

    public ClassSelectInventory(Mystica main){
        this.main = main;
        customInventoryManager = main.getInventoryManager();
        classSetter = main.getClassSetter();
    }

    public Inventory openClassSelect(int index){

        //inv.setItem(22, getItem(Material.LIME_DYE, 0,"Select"));

        //inv.setItem(15, getItem(Material.ARROW,0,"Next"));

        //inv.setItem(11, getItem(Material.ARROW, 0, "Previous"));

        String title = "something went wrong";

        //old is "\uF809\uF808\uF804\uE06F" + "\uF802\uE070" +  "\uF82A\uF828\uF825\uF80D\uE071" + "\uF802\uE072";
        // "\uF82A\uF828%s" "\uF825%s", "\uF80D%s",
        // second half is offset space 40, space 5, space -256 which = -206


        switch (index){
            case 0:{
                //title = "\uF809\uF808\uF804\uE06F" + "\uF801\uE070" +  "\uF82A\uF828\uF825\uF80D\uE071" + "\uF801\uE072";
                //title = "\uF809\uF808\uF804\uE06F" + "\uF801\uE070\uF821" +  "\uF82A\uF828\uF825\uF80D\uE071" + "\uF801\uE072";
                title = "\uF809\uF808\uF804\uE06F" + "\uF801\uE070\uF821" +  "\uF82A\uF826\uF825\uF80D\uE071" + "\uF801\uE072";

                break;
            }
            case 1:{
                //title = "elementalist";
                title = "\uF809\uF808\uF804\uE073" + "\uF801\uE074\uF821" +  "\uF82A\uF826\uF825\uF80D\uE075" + "\uF801\uE076";
                //inv.setItem(13, getElementalistItem());
                break;
            }
            case 2:{
                //title = "mystic";
                title = "\uF809\uF808\uF804\uE077" + "\uF801\uE078\uF821" +  "\uF82A\uF826\uF825\uF80D\uE079" + "\uF801\uE07A";
                //inv.setItem(13, getMysticItem());
                break;
            }
            case 3:{
                //title = "paladin";
                title = "\uF809\uF808\uF804\uE07B" + "\uF801\uE07C\uF821" +  "\uF82A\uF826\uF825\uF80D\uE07D" + "\uF801\uE07E";
                //inv.setItem(13, getPaladinItem());
                break;
            }
            case 4:{
                //title = "ranger";
                title = "\uF809\uF808\uF804\uE07F" + "\uF801\uE080\uF821" +  "\uF82A\uF826\uF825\uF80D\uE081" + "\uF801\uE082";
                //inv.setItem(13, getRangerItem());
                break;
            }
            case 5:{
                //title = "shadow knight";
                title = "\uF809\uF808\uF804\uE083" + "\uF801\uE084\uF821" +  "\uF82A\uF826\uF825\uF80D\uE085" + "\uF801\uE086";
                //inv.setItem(13, getShadowKnightItem());
                break;
            }
            case 6:{
                title = "\uF809\uF808\uF804\uE087" + "\uF801\uE088\uF821" +  "\uF82A\uF826\uF825\uF80D\uE089" + "\uF801\uE08A";
                //inv.setItem(13, getWarriorItem());
                break;
            }

        }

        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + title);

        //arrows
        //inv.setItem(45, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(53, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        //select
        //inv.setItem(48, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(49, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(50, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));



        return inv;
    }

    @EventHandler
    public void classClicks(InventoryClickEvent event){

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE06F" + "\uF801\uE070\uF821" +  "\uF82A\uF826\uF825\uF80D\uE071" + "\uF801\uE072")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE073" + "\uF801\uE074\uF821" +  "\uF82A\uF826\uF825\uF80D\uE075" + "\uF801\uE076")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE077" + "\uF801\uE078\uF821" +  "\uF82A\uF826\uF825\uF80D\uE079" + "\uF801\uE07A")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE07B" + "\uF801\uE07C\uF821" +  "\uF82A\uF826\uF825\uF80D\uE07D" + "\uF801\uE07E")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE07F" + "\uF801\uE080\uF821" +  "\uF82A\uF826\uF825\uF80D\uE081" + "\uF801\uE082")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE083" + "\uF801\uE084\uF821" +  "\uF82A\uF826\uF825\uF80D\uE085" + "\uF801\uE086")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF809\uF808\uF804\uE087" + "\uF801\uE088\uF821" +  "\uF82A\uF826\uF825\uF80D\uE089" + "\uF801\uE08A")){

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

                    case ("\uF809\uF808\uF804\uE06F" + "\uF801\uE070\uF821" +  "\uF82A\uF826\uF825\uF80D\uE071" + "\uF801\uE072"):
                    {
                        classSetter.setClass(player, PlayerClass.NONE);
                        player.closeInventory();
                        return;

                    }
                    case ("\uF809\uF808\uF804\uE073" + "\uF801\uE074\uF821" +  "\uF82A\uF826\uF825\uF80D\uE075" + "\uF801\uE076"):
                    {
                        classSetter.setClass(player, PlayerClass.Elementalist);
                        player.closeInventory();
                        return;
                    }
                    case ("\uF809\uF808\uF804\uE077" + "\uF801\uE078\uF821" +  "\uF82A\uF826\uF825\uF80D\uE079" + "\uF801\uE07A"):
                    {
                        classSetter.setClass(player, PlayerClass.Mystic);
                        player.closeInventory();
                        return;
                    }
                    case ("\uF809\uF808\uF804\uE07B" + "\uF801\uE07C\uF821" +  "\uF82A\uF826\uF825\uF80D\uE07D" + "\uF801\uE07E"):
                    {
                        classSetter.setClass(player, PlayerClass.Paladin);
                        player.closeInventory();
                        return;
                    }
                    case ("\uF809\uF808\uF804\uE07F" + "\uF801\uE080\uF821" +  "\uF82A\uF826\uF825\uF80D\uE081" + "\uF801\uE082"):
                    {
                        classSetter.setClass(player, PlayerClass.Ranger);
                        player.closeInventory();
                        return;
                    }
                    case ("\uF809\uF808\uF804\uE083" + "\uF801\uE084\uF821" +  "\uF82A\uF826\uF825\uF80D\uE085" + "\uF801\uE086"):
                    {
                        classSetter.setClass(player, PlayerClass.Shadow_Knight);
                        player.closeInventory();
                        return;
                    }
                    case ("\uF809\uF808\uF804\uE087" + "\uF801\uE088\uF821" +  "\uF82A\uF826\uF825\uF80D\uE089" + "\uF801\uE08A"):
                    {
                        classSetter.setClass(player, PlayerClass.Warrior);
                        player.closeInventory();
                        return;
                    }
                }

                Bukkit.getLogger().info("something went wrong");
                return;
            }



            int index = customInventoryManager.getClassIndex(player);

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

            customInventoryManager.setClassIndex(player, index);

            player.openInventory(new ClassSelectInventory(main).openClassSelect(index));
        }

    }


}
