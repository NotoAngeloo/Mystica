package me.angeloo.mystica.Utility.Listeners;


import me.angeloo.mystica.Components.Items.Equipment.EquipmentDisplayRenderer;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryEventListener implements Listener {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final EquipmentDisplayRenderer equipmentDisplayRenderer;
    /*private final BagEquipmentFunctions bagEquipmentFunctions;
    private final GenericDiscard genericDiscard;
    private final InventoryItemGetter itemGetter;
    private final CustomInventoryManager inventoryManager;
    private final DungeonSelect dungeonSelect;
    private final PartyInventory partyInventory;
    private final AbilityInventory abilityInventory;
    private final DisplayWeapons displayWeapons;*/

    public InventoryEventListener(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentDisplayRenderer = main.getEquipmentDisplayRenderer();
        //itemGetter = main.getItemGetter();
        /*inventoryManager = main.getInventoryManager();
        dungeonSelect = main.getDungeonSelect();
        partyInventory = main.getPartyInventory();
        abilityInventory = main.getAbilityInventory();
        displayWeapons = main.getDisplayWeapons();
        bagEquipmentFunctions = main.getBagEquipmentFunctions();
        genericDiscard = main.getGenericDiscard();*/
    }

    /*@EventHandler
    public void bagClicks(InventoryClickEvent event){

        if(event.getView().getTitle().equalsIgnoreCase(org.bukkit.ChatColor.WHITE + "\uF808" + "\uE05C")){

            //|| event.getView().getTitle().equalsIgnoreCase(ChatColor.WHITE + "\uF807" + "\uE05D" + "\uF827" + "\uF80D" + "\uF82B\uF828\uF826" + "\uE05C")

            event.setCancelled(true);

            //if player clicks an item, move it to the top inventory with options to dismantle or move

            Player player = (Player) event.getWhoClicked();

            ItemStack[] oldContents = player.getInventory().getContents();

            Inventory bottomInv = event.getView().getBottomInventory();

            int slot = event.getSlot();

            if(event.getClickedInventory() == bottomInv){

                if(slot < 9){
                    return;
                }

                ItemStack item = event.getCurrentItem();

                if(item == null){
                    return;
                }


                //get the type of item it is, then switch statement for type
                MysticaItemFormat type = getItemType(item);

                //pass through inventory to not change the uuids of the items displayed
                switch (type){
                    case EQUIPMENT -> {
                        bagEquipmentFunctions.open(player, item, oldContents);
                        return;
                    }
                    default -> {
                        genericDiscard.open(player, item, oldContents);
                        return;
                    }
                }
                
            }

        }

    }*/

    @EventHandler
    public void invOpen(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();
        player.setItemOnCursor(null);
        player.getInventory().setItemInMainHand(null);
    }






    @EventHandler
    public void guiClose(InventoryCloseEvent event){

        Player player = (Player) event.getPlayer();

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                String openTitle = player.getOpenInventory().getTitle();

                if(!openTitle.equalsIgnoreCase("crafting")){
                    return;
                }

                equipmentDisplayRenderer.renderSheathedWeapons(player);
            }
        }.runTaskLaterAsynchronously(main, 1);




    }

    /*@EventHandler
    public void clickBagSlot(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE05C")){
            return;
        }

        Inventory clickedInv = event.getClickedInventory();
        Inventory botInv = event.getView().getBottomInventory();

        if(clickedInv != botInv){
            return;
        }

        List<Integer> bagSlots = new ArrayList<>();
        bagSlots.add(0);
        bagSlots.add(1);
        bagSlots.add(2);
        bagSlots.add(3);
        bagSlots.add(4);
        bagSlots.add(5);
        bagSlots.add(6);
        bagSlots.add(7);
        bagSlots.add(8);

        if(!bagSlots.contains(event.getSlot())){
            return;
        }

        event.setCancelled(true);

    }*/


}
