package me.angeloo.mystica.Components.Guis.Abilities;

import me.angeloo.mystica.Components.Items.MysticalCrystal;
import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ClassSetter;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.List;

public class ClassSelectInventory implements Listener {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final CustomInventoryManager customInventoryManager;
    private final DisplayWeapons displayWeapons;
    private final ClassSetter classSetter;

    public ClassSelectInventory(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        displayWeapons = main.getDisplayWeapons();
        customInventoryManager = main.getInventoryManager();
        classSetter = main.getClassSetter();
    }

    public void openClassSelect(Player player){

        int index = customInventoryManager.getClassIndex(player);


        String title = "something went wrong";


        switch (index) {
            case 0 -> {
                //assassin
                //-12, top, -256+72, bottom
                title = "\uF808\uF804" + "\uE06F" + "\uF80D" + "\uF82B\uF828" + "\uE070";

            }
            case 1 -> {
                //title = "elementalist";
                title = "\uF808\uF804" + "\uE071" + "\uF80D" + "\uF82B\uF828" + "\uE072";
                //inv.setItem(13, getElementalistItem());
            }
            case 2 -> {
                //title = "mystic";
                title = "\uF808\uF804" + "\uE073" + "\uF80D" + "\uF82B\uF828" + "\uE074";
                //inv.setItem(13, getMysticItem());
            }
            case 3 -> {
                //title = "paladin";
                title = "\uF808\uF804" + "\uE075" + "\uF80D" + "\uF82B\uF828" + "\uE076";
                //paladin bottom 2 is "\uE07D"; when i have it
            }
            case 4 -> {
                //title = "ranger";
                title = "\uF808\uF804" + "\uE077" + "\uF80D" + "\uF82B\uF828" + "\uE078";
            }
            case 5 -> {
                //title = "shadow knight";
                title = "\uF808\uF804" + "\uE079" + "\uF80D" + "\uF82B\uF828" + "\uE07A";
            }
            case 6 -> {
                //title = "warrior"
                title = "\uF808\uF804" + "\uE07B" + "\uF80D" + "\uF82B\uF828" + "\uE07C";
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

        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);


        /*player.getInventory().setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        player.getInventory().setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(4, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));*/
    }

    @EventHandler
    public void classClicks(InventoryClickEvent event){

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE06F" + "\uF80D" + "\uF82B\uF828" + "\uE070")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE071" + "\uF80D" + "\uF82B\uF828" + "\uE072")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE073" + "\uF80D" + "\uF82B\uF828" + "\uE074")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE075" + "\uF80D" + "\uF82B\uF828" + "\uE076")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE077" + "\uF80D" + "\uF82B\uF828" + "\uE078")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE079" + "\uF80D" + "\uF82B\uF828" + "\uE07A")
                || event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF808\uF804" + "\uE07B" + "\uF80D" + "\uF82B\uF828" + "\uE07C")){

            event.setCancelled(true);

            if(event.getClickedInventory() == null){
                return;
            }

            Inventory inv = event.getView().getBottomInventory();

            if(event.getClickedInventory() != inv){
                return;
            }

            String title = event.getView().getTitle();

            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();

            List<Integer> selectSlots = new ArrayList<>();
            selectSlots.add(3);
            selectSlots.add(4);
            selectSlots.add(5);

            if(selectSlots.contains(slot)){

                boolean cost = costsCrystal(player);

                //logic to determine if costs
                if(cost && !hasClassCrystal(player)){
                    player.closeInventory();
                    player.sendMessage("Requires Mystical Crystal");
                    return;
                }

                String colorlessTitle = title.replaceAll("§.", "");

                switch (colorlessTitle) {
                    case ("\uF808\uF804" + "\uE06F" + "\uF80D" + "\uF82B\uF828" + "\uE070") -> {
                        classSetter.setClass(player, PlayerClass.Assassin);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }

                        return;

                    }
                    case ("\uF808\uF804" + "\uE071" + "\uF80D" + "\uF82B\uF828" + "\uE072") -> {
                        classSetter.setClass(player, PlayerClass.Elementalist);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }
                        return;
                    }
                    case ("\uF808\uF804" + "\uE073" + "\uF80D" + "\uF82B\uF828" + "\uE074") -> {
                        classSetter.setClass(player, PlayerClass.Mystic);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }
                        return;
                    }
                    case ("\uF808\uF804" + "\uE075" + "\uF80D" + "\uF82B\uF828" + "\uE076"), ("\uF808\uF804" + "\uE075" + "\uF80D" + "\uF82B\uF828" + "\uE07D") -> {
                        classSetter.setClass(player, PlayerClass.Paladin);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }
                        return;
                    }

                    case ("\uF808\uF804" + "\uE077" + "\uF80D" + "\uF82B\uF828" + "\uE078") -> {
                        classSetter.setClass(player, PlayerClass.Ranger);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }
                        return;
                    }
                    case ("\uF808\uF804" + "\uE079" + "\uF80D" + "\uF82B\uF828" + "\uE07A") -> {
                        classSetter.setClass(player, PlayerClass.Shadow_Knight);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }
                        return;
                    }
                    case ("\uF808\uF804" + "\uE07B" + "\uF80D" + "\uF82B\uF828" + "\uE07C") -> {
                        classSetter.setClass(player, PlayerClass.Warrior);
                        player.closeInventory();

                        if(cost){
                            profileManager.getAnyProfile(player).getMysticaBagCollection().removeItemsFromMultipleBags(new MysticalCrystal(1));
                        }
                        return;
                    }
                }

                Bukkit.getLogger().info("something went wrong");
                return;
            }



            int index = customInventoryManager.getClassIndex(player);

            if(slot == 7){
                index++;
            }

            if(slot == 1){
                index--;
            }

            //based on number opf classes
            if(index<0){
                index = 6;
            }

            if(index>6){
                index = 0;
            }

            customInventoryManager.setClassIndex(player, index);

            openClassSelect(player);
        }

    }

    private boolean costsCrystal(Player player){

        //if none, will ALWAYS cost
        if(profileManager.getAnyProfile(player).getPlayerClass().equals(PlayerClass.NONE)){
            return true;
        }

        //but if not none, will cost nothing if they are level 1
        return profileManager.getAnyProfile(player).getStats().getLevel() != 1;
    }


    private boolean hasClassCrystal(Player player){
        return profileManager.getAnyProfile(player).getMysticaBagCollection().getItemAmount(new MysticalCrystal(1)) > 0;
    }


}
