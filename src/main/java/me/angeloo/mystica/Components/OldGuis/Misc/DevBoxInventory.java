package me.angeloo.mystica.Components.OldGuis.Misc;

import org.bukkit.event.Listener;

public class DevBoxInventory implements Listener {

    /*private final ProfileManager profileManager;
    private final CustomInventoryManager inventoryManager;

    public DevBoxInventory(Mystica main){
        profileManager = main.getProfileManager();
        inventoryManager = main.getInventoryManager();
    }

    public void open(Player player){

        //-8, png, +8
        String title = ChatColor.WHITE + "\uF808" + "\uE07E" + "\uF828";


        title = inventoryManager.addBagPng(title);

        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        inv.setItem(0, new SoulStone(1).build());
        inv.setItem(1, new MysticalCrystal(1).build());
        inv.setItem(2, new UnidentifiedItem(EquipmentSlot.WEAPON, 1, 1).build());
        inv.setItem(3, new UnidentifiedItem(EquipmentSlot.HEAD, 1, 1).build());
        inv.setItem(4, new UnidentifiedItem(EquipmentSlot.CHEST, 1, 1).build());
        inv.setItem(5, new UnidentifiedItem(EquipmentSlot.LEGS, 1, 1).build());
        inv.setItem(6, new UnidentifiedItem(EquipmentSlot.BOOTS, 1, 1).build());
        inv.setItem(7, new UnidentifiedItem(EquipmentSlot.Random, 1, 1).build());

        player.openInventory(inv);

        profileManager.getAnyProfile(player).getMysticaBagCollection().getBag(inventoryManager.getBagIndex(player)).displayBagItems(player);
    }

    @EventHandler
    public void devBoxClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE07E")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        MysticaBagCollection collection = profileManager.getAnyProfile(player).getMysticaBagCollection();
        MysticaBag currentBag = collection.getBag(inventoryManager.getBagIndex(player));

        if(event.getClickedInventory() == null){
            return;
        }

        if(event.getClickedInventory().equals(topInv)){

            //add items to bag
            ItemStack item = event.getCurrentItem();

            if(item == null){
                return;
            }

            //Bukkit.getLogger().info(String.valueOf(item.getItemMeta()));

            MysticaItem mysticaItem = MysticaItem.toMysticaItem(item);

            if(mysticaItem == null){
                return;
            }

            collection.addToFirstBag(mysticaItem);

            open(player);
            return;
        }
    }*/

}
