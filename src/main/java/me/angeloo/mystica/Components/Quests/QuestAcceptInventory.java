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

    public void openQuestAccept(Player player, String[] text){

        //first check if this line of text has been generated already

        //replaces "\n" with newline char
        //text = text.replaceAll("\\\\n", "\n");

        //in a task cuz needs to calculate something expensive
        //String finalText = text;
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                //this is a builder because i would like a png to be in the background of the text
                StringBuilder questText = new StringBuilder();

                //check if /n to increase line number

                questText.append(textGenerator.getInventoryText(text));

                Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + String.valueOf(questText));

                inv.setItem(0, new ItemStack(Material.EMERALD));
                inv.setItem(8, new ItemStack(Material.EMERALD));

                Bukkit.getScheduler().runTask(main, ()->{
                        player.openInventory(inv);
                player.getInventory().clear();
                displayWeapons.displayArmor(player);
                });

            }
        }.runTaskAsynchronously(main);






    }



}
