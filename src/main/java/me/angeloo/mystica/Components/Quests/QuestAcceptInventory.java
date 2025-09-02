package me.angeloo.mystica.Components.Quests;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class QuestAcceptInventory {

    private final Mystica main;

    private final DisplayWeapons displayWeapons;

    private final QuestInventoryTextGenerator textGenerator;

    public QuestAcceptInventory(Mystica main){
        this.main = main;
        displayWeapons = main.getDisplayWeapons();
        textGenerator = new QuestInventoryTextGenerator();
    }

    /*this accepts String because want to be able to be edited with yml.

    example

    Quest Name:

    Line 1 (negative space)

    Line 2 (negative space)

    Line 3 (negative space)

    */

    public void openQuestAccept(Player player, Quest quest){


        //in a task cuz needs to calculate something expensive

        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //this is a builder because i would like a png to be in the background of the text
                StringBuilder questText = new StringBuilder();

                //negative space before
                //-110
                questText.append("\uF80B\uF80A\uF808\uF806");

                //parchment png
                questText.append("\uE87A");

                //-256
                questText.append("\uF80D");
                //-126
                questText.append("\uF80B\uF80A\uF809\uF808\uF806");
                //accept/complete
                questText.append("\uE87B");

                //-256
                questText.append("\uF80D");
                //-100
                questText.append("\uF80B\uF80A\uF804");

                questText.append(textGenerator.getInventoryText(quest.getDescription()));

                Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + String.valueOf(questText));

                //inv.setItem(0, new ItemStack(Material.EMERALD));
                //inv.setItem(8, new ItemStack(Material.EMERALD));

                Bukkit.getScheduler().runTask(main, ()->{
                        player.openInventory(inv);
                player.getInventory().clear();
                displayWeapons.displayArmor(player);

                /*player.getInventory().setItem(27, new ItemStack(Material.EMERALD));
                player.getInventory().setItem(35, new ItemStack(Material.EMERALD));

                player.getInventory().setItem(0, new ItemStack(Material.EMERALD));
                player.getInventory().setItem(8, new ItemStack(Material.EMERALD));

                    player.getInventory().setItem(29, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    player.getInventory().setItem(30, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    player.getInventory().setItem(31, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    player.getInventory().setItem(32, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    player.getInventory().setItem(33, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    player.getInventory().setItem(2, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                player.getInventory().setItem(3, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                player.getInventory().setItem(4, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                player.getInventory().setItem(5, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));
                    player.getInventory().setItem(6, new ItemStack(Material.WHITE_STAINED_GLASS_PANE));*/
                });

            }
        }.runTaskAsynchronously(main);






    }



}
