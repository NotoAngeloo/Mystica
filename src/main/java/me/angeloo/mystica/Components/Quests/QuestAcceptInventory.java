package me.angeloo.mystica.Components.Quests;

import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class QuestAcceptInventory implements Listener {

    private final Mystica main;

    private final DisplayWeapons displayWeapons;
    private final QuestInventoryTextGenerator textGenerator;

    private final Map<UUID, Quest> currentViewedQuest = new HashMap<>();
    private final Map<UUID, Integer> questDescriptionIndex = new HashMap<>();

    public QuestAcceptInventory(Mystica main){
        this.main = main;
        displayWeapons = main.getDisplayWeapons();
        textGenerator = new QuestInventoryTextGenerator();
    }

    public void openQuestAccept(Player player, Quest quest){

        questDescriptionIndex.put(player.getUniqueId(), 0);
        currentViewedQuest.put(player.getUniqueId(), quest);

        //in a task cuz needs to calculate something expensive

        new BukkitRunnable(){
            @Override
            public void run(){

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

                //description can hav max of 14 lines, if has more need to index them
                List<String> viewableDescription = getViewableDescription(player, quest);

                questText.append(textGenerator.getInventoryText(viewableDescription));

                Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + String.valueOf(questText));



                Bukkit.getScheduler().runTask(main, ()->
                {
                    player.openInventory(inv);
                    player.getInventory().clear();
                    displayWeapons.displayArmor(player);

                    //scroll buttons to be, maybe make these only show up with lengthy quests

                    if(questDescriptionIndex.get(player.getUniqueId())!=0){
                        player.getInventory().setItem(35, new ItemStack(Material.EMERALD));
                    }



                    if(!maxScrollReached(player)){
                        player.getInventory().setItem(8, new ItemStack(Material.EMERALD));
                    }



                });

            }
        }.runTaskAsynchronously(main);


    }

    private List<String> getViewableDescription(Player player, Quest quest){

        String title = quest.getName();
        List<String> questDescription = quest.getDescription();
        List<String> viewableDescription = new ArrayList<>();
        int index = questDescriptionIndex.getOrDefault(player.getUniqueId(), 0);

        viewableDescription.add(title);
        viewableDescription.add("");

        //12 because title is always 1, blank is always 2
        int maxScroll = Math.max(0, questDescription.size() - 12);
        int start = Math.min(index, maxScroll);

        int end = Math.min(start + 12, questDescription.size());

        for (int i = start; i < end; i++) {
            viewableDescription.add(questDescription.get(i));
        }

        return viewableDescription;
    }



    @EventHandler
    public void questAcceptCLick(InventoryClickEvent event){

        //the parchment
        if(event.getView().getTitle().contains("\uE87A")){
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            if(event.getClickedInventory() == bottomInv){

                int slot = event.getSlot();

                //down
                if(slot==8){

                    //compare description to what is shown first
                    if(maxScrollReached(player)){
                        return;
                    }

                    questIndexDown(player);

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

                    //description can hav max of 14 lines, if has more need to index them
                    List<String> viewableDescription = getViewableDescription(player, getCurrentViewedQuest(player));

                    questText.append(textGenerator.getInventoryText(viewableDescription));

                    event.getView().setTitle(ChatColor.WHITE + String.valueOf(questText));

                    if(maxScrollReached(player)){
                        player.getInventory().setItem(8, null);
                    }
                    else{
                        player.getInventory().setItem(8, new ItemStack(Material.EMERALD));
                    }

                    if(questDescriptionIndex.get(player.getUniqueId())!=0){
                        player.getInventory().setItem(35, new ItemStack(Material.EMERALD));
                    }
                    else{
                        player.getInventory().setItem(35, null);
                    }

                    return;
                }

                //up
                if(slot==35){

                    questIndexUp(player);

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

                    List<String> viewableDescription = getViewableDescription(player, getCurrentViewedQuest(player));

                    questText.append(textGenerator.getInventoryText(viewableDescription));

                    event.getView().setTitle(ChatColor.WHITE + String.valueOf(questText));

                    if(questDescriptionIndex.get(player.getUniqueId())!=0){
                        player.getInventory().setItem(35, new ItemStack(Material.EMERALD));
                    }
                    else{
                        player.getInventory().setItem(35, null);
                    }

                    if(maxScrollReached(player)){
                        player.getInventory().setItem(8, null);
                    }
                    else{
                        player.getInventory().setItem(8, new ItemStack(Material.EMERALD));
                    }


                    return;
                }

                return;
            }

        }


    }

    //don't do if at end of decription
    private void questIndexDown(Player player){

        if(questDescriptionIndex.containsKey(player.getUniqueId())){

            int index = questDescriptionIndex.get(player.getUniqueId());
            questDescriptionIndex.put(player.getUniqueId(), index + 1);
        }

    }

    private void questIndexUp(Player player){

        if(questDescriptionIndex.containsKey(player.getUniqueId())){
            int index = questDescriptionIndex.get(player.getUniqueId());

            if(index == 0){
                return;
            }

            questDescriptionIndex.put(player.getUniqueId(), index - 1);
        }

    }

    private Quest getCurrentViewedQuest(Player player){
        return currentViewedQuest.get(player.getUniqueId());
    }

    private boolean maxScrollReached(Player player){

        Quest quest = getCurrentViewedQuest(player);

        int index = questDescriptionIndex.getOrDefault(player.getUniqueId(), 0);

        return  index >= maxIndex(quest.getDescription());
    }

    private int maxIndex(List<String> description){
        return Math.max(0, description.size() - 12);
    }

    //make actual buttons later

}
