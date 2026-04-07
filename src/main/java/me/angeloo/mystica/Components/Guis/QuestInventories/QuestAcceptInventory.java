package me.angeloo.mystica.Components.Guis.QuestInventories;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Guis.InventoryTextGenerator;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Quests.Progress.QuestProgress;
import me.angeloo.mystica.Components.Quests.Quest;
import me.angeloo.mystica.Components.Quests.Rewards.ItemReward;
import me.angeloo.mystica.Components.Quests.Rewards.QuestReward;
import me.angeloo.mystica.CustomEvents.UpdateSpeakQuestProgressEvent;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.InventoryItemGetter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class QuestAcceptInventory implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;

    private final DisplayWeapons displayWeapons;
    private final InventoryTextGenerator textGenerator;
    private final InventoryItemGetter itemGetter;

    private final Map<UUID, Quest> currentViewedQuest = new HashMap<>();
    private final Map<UUID, Integer> questDescriptionIndex = new HashMap<>();

    public QuestAcceptInventory(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        displayWeapons = main.getDisplayWeapons();
        textGenerator = main.getInventoryManager().getTextGenerator();
        itemGetter = main.getItemGetter();
    }

    public void openQuestAccept(Player player, Quest quest){

        questDescriptionIndex.put(player.getUniqueId(), 0);
        currentViewedQuest.put(player.getUniqueId(), quest);

        //check progress on the quest to determine



        //put a 3rd thing in the middle when they are on the quest and not finished

        //needs to a check to see if turned in yet

        //in a task cuz needs to calculate something expensive
        int finalQuestStage = getQuestStage(player, quest);

        //Bukkit.getLogger().info(String.valueOf(finalQuestStage));

        if(finalQuestStage == 3){
            return;
        }

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
                switch (finalQuestStage) {
                    case 0 -> {
                        questText.append("\uE87B");
                    }
                    case 1 -> {
                        questText.append("\uE87D");
                    }
                    case 2 -> {
                        questText.append("\uE87C");

                    }
                }

                //-256
                questText.append("\uF80D");
                //-100
                questText.append("\uF80B\uF80A\uF804");


                List<String> viewableDescription = getViewableDescription(player, quest, finalQuestStage);

                questText.append(textGenerator.getInventoryText(viewableDescription));

                Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + String.valueOf(questText));

                Bukkit.getScheduler().runTask(main, ()->
                {
                    player.openInventory(inv);
                    player.getInventory().clear();
                    displayWeapons.displayArmor(player);


                    if(questDescriptionIndex.get(player.getUniqueId())!=0){
                        player.getInventory().setItem(35, new ItemStack(Material.EMERALD));
                    }


                    if(!maxScrollReached(player)){
                        player.getInventory().setItem(8, new ItemStack(Material.EMERALD));
                    }


                    player.getInventory().setItem(27, getRewardItem(quest));

                });

            }
        }.runTaskAsynchronously(main);


    }

    private List<String> getViewableDescription(Player player, Quest quest, int stage){

        String title = quest.getName();

        List<String> questDescription = quest.getProgress();

        switch (stage){
            case 0 ->{
                questDescription = quest.getDescription();
            }
            case 1 ->{
                questDescription = quest.getProgress();
            }
            case 2 ->{
                questDescription = quest.getCompleted();
            }
        }

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

        //the parchment, needs to ALSO not contain the title, because this is reused in pick quest. maybe it shouldn't
        if(event.getView().getTitle().contains("\uE87A") && !event.getView().getTitle().contains("\uE87E")){
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            int stage = getQuestStage(player, getCurrentViewedQuest(player));

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
                    switch (stage){
                        case 0 ->{
                            questText.append("\uE87B");

                        }
                        case 1 -> {
                            questText.append("\uE87D");
                        }
                        case 2 ->{
                            questText.append("\uE87C");
                        }
                    }

                    //-256
                    questText.append("\uF80D");
                    //-100
                    questText.append("\uF80B\uF80A\uF804");

                    List<String> viewableDescription = getViewableDescription(player, getCurrentViewedQuest(player), stage);

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
                    switch (stage){
                        case 0 ->{
                            questText.append("\uE87B");

                        }
                        case 1 ->{
                            questText.append("\uE87D");
                        }
                        case 2 ->{
                            questText.append("\uE87C");
                        }
                    }

                    //-256
                    questText.append("\uF80D");
                    //-100
                    questText.append("\uF80B\uF80A\uF804");

                    List<String> viewableDescription = getViewableDescription(player, getCurrentViewedQuest(player), stage);

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

                List<Integer> acceptSlots = new ArrayList<>();

                acceptSlots.add(2);
                acceptSlots.add(3);
                acceptSlots.add(4);
                acceptSlots.add(5);
                acceptSlots.add(6);
                acceptSlots.add(34);
                acceptSlots.add(33);
                acceptSlots.add(32);
                acceptSlots.add(31);
                acceptSlots.add(30);
                acceptSlots.add(29);

                if(acceptSlots.contains(slot)){

                    Quest quest = getCurrentViewedQuest(player);

                    switch (stage){
                        case 0 ->{

                            QuestProgress progress = new QuestProgress(quest);

                            profileManager.getAnyProfile(player).addQuestProgress(progress);

                            player.closeInventory();

                            //trigger event for the one who gave the quest
                            MythicMob mythicMob = MythicBukkit.inst().getAPIHelper().getMythicMob(quest.getGiverId());

                            if(mythicMob != null){
                                Bukkit.getServer().getPluginManager().callEvent(new UpdateSpeakQuestProgressEvent(player, mythicMob));
                            }


                            return;
                        }
                        case 2 ->{
                            player.closeInventory();
                            //rewards
                            profileManager.getAnyProfile(player).getQuestProgressMap().get(quest.getId()).setRewarded();

                            for(QuestReward reward : quest.getRewards()){

                                if(reward instanceof ItemReward itemReward){
                                    MysticaItem item = itemReward.getItem();
                                    profileManager.getAnyProfile(player).getMysticaBagCollection().addToFirstBag(item);
                                }

                            }


                        }
                    }


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

    private int getQuestStage(Player player, Quest quest){
        int questStage = 0;

        if(profileManager.getAnyProfile(player).getQuestProgressMap().containsKey(quest.getId())){
            questStage = 1;

            QuestProgress progress = profileManager.getAnyProfile(player).getQuestProgressMap().get(quest.getId());

            if(progress.isComplete()){
                questStage = 2;
            }

            if(progress.isRewarded()){
                questStage = 3;
            }
        }

        return questStage;
    }

    private ItemStack getRewardItem(Quest quest){

        ItemStack rewardItem = itemGetter.getItem(Material.EMERALD, 0, ChatColor.RESET + "Rewards:");

        List<String> lores = new ArrayList<>();

        lores.add("");

        for(QuestReward reward : quest.getRewards()){

            if(reward instanceof ItemReward itemReward){
                MysticaItem mi = itemReward.getItem();

                lores.add(ChatColor.WHITE+ "- " + mi.identifier());
            }
        }

        ItemMeta meta = rewardItem.getItemMeta();
        assert meta != null;
        meta.setLore(lores);
        rewardItem.setItemMeta(meta);

        return rewardItem;

    }

    public InventoryTextGenerator getTextGenerator(){
        return this.textGenerator;
    }

}
