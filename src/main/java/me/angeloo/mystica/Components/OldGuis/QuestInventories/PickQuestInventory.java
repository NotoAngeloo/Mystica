package me.angeloo.mystica.Components.OldGuis.QuestInventories;

import org.bukkit.event.Listener;

public class PickQuestInventory implements Listener {

    /*private final Mystica main;
    private final ProfileManager profileManager;
    private final QuestManager questManager;
    private final DisplayWeapons displayWeapons;
    private final QuestAcceptInventory questAcceptInventory;
    //private InventoryTextGenerator textGenerator;

    //private final Map<String, Shop>

    public PickQuestInventory(Mystica main, QuestAcceptInventory questAcceptInventory){
        this.main = main;
        profileManager = main.getProfileManager();
        questManager = main.getQuestManager();
        displayWeapons = main.getDisplayWeapons();
        this.questAcceptInventory = questAcceptInventory;
        //textGenerator = questAcceptInventory.getTextGenerator();
    }

    //which npc does it
    public void open(Player player, MythicMob questGiver){

        String giverId = questGiver.getInternalName();
        List<Quest> quests = questManager.getQuestsForNpc(giverId);
        PlayerProfile profile = (PlayerProfile) profileManager.getAnyProfile(player);

        StringBuilder title = new StringBuilder();

        //negative space before
        //-110
        title.append("\uF80B\uF80A\uF808\uF806");

        //parchment png
        title.append("\uE87A");

        //-256
        title.append("\uF80D");
        //-126
        title.append("\uF80B\uF80A\uF809\uF808\uF806");
        //name bar
        title.append("\uE87E");

        //-256
        title.append("\uF80D");
        //-100
        title.append("\uF80B\uF80A\uF804");


        title.append(getQuestText(questGiver));

        Inventory inv = Bukkit.createInventory(null, 9*6, ChatColor.WHITE + String.valueOf(title));

        int questAmount = 0;
        Quest currentQuest = null;
        int invSlot = 9;
        for(Quest quest : quests){

            currentQuest = quest;

            if(invSlot>=53){
                break;
            }

            if(quest.canStart(profile)){

                QuestProgress progress = profile.getQuestProgressMap().get(quest.getId());

                if(progress != null){
                    if(progress.isRewarded()){
                        continue;
                    }
                }

                inv.setItem(invSlot, getQuestItem(quest));
                invSlot++;
                questAmount++;
            }

        }

        //if only 1 quest, open that instead
        if(questAmount == 1){
            questAcceptInventory.openQuestAccept(player, currentQuest);
            return;
        }

        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);



        //down here are other functions besides quests, like shop. this should be a different gui
        //player.getInventory().setItem(9, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(11, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(27, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(29, new ItemStack(Material.EMERALD));
        //player.getInventory().setItem(35, new ItemStack(Material.EMERALD));
    }

    @EventHandler
    public void pickQuestClicks(InventoryClickEvent event){

        //title bar thingy
        if(!event.getView().getTitle().contains("\uE87E")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        if(event.getClickedInventory() == topInv){
            //Bukkit.getLogger().info(String.valueOf(slot));

            ItemStack item = event.getCurrentItem();

            if(item == null){
                return;
            }

            ItemMeta meta = item.getItemMeta();

            if(meta == null){
                return;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();

            String questId = container.get(new NamespacedKey(main, "quest_id"), PersistentDataType.STRING);

            Quest quest = questManager.getQuest(questId);

            if(quest == null){
                return;
            }


            questAcceptInventory.openQuestAccept(player, quest);

        }

    }

    private ItemStack getQuestItem(Quest quest){
        ItemStack item = new ItemStack(Material.EMERALD);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.WHITE + quest.getName());

        NamespacedKey key = new NamespacedKey(main, "quest_id");

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, quest.getId());

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setCustomModelData(0);

        item.setItemMeta(meta);
        return item;
    }



    private String getQuestText(MythicMob questGiver){
        List<String> invText = new ArrayList<>();

        String mobName = questGiver.getDisplayName().get();
        invText.add(mobName);

        return textGenerator.getInventoryText(invText);
    }*/

}
