package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.MatchMakingManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class DungeonSelect implements Listener {

    private final ProfileManager profileManager;
    private final CustomInventoryManager customInventoryManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final MatchMakingManager matchMakingManager;

    public DungeonSelect(Mystica main){
        profileManager = main.getProfileManager();
        customInventoryManager = main.getInventoryManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        matchMakingManager = main.getMatchMakingManager();
    }

    public void openDungeonSelect(Player player){

        String dungeonSplash;

        int index = customInventoryManager.getDungeonIndex(player);

        switch (index) {
            case 0 ->{
                //heart
                dungeonSplash = "\uE090";
            }
            case 1 ->{
                //weber
                dungeonSplash = "\uE091";
            }
            case 2 -> {
                //lindwyrm
                dungeonSplash = "\uE08E";
            }
            case 3 -> {
                //coersica
                dungeonSplash = "\uE08F";
            }
            default -> {
                return;
            }
        }

        //dungeon, -256, +78
        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF807" + dungeonSplash + "\uF80D" + "\uF82B\uF828\uF826" + "\uE08B");

        player.openInventory(inv);

    }

    @EventHandler
    public void dungeonClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE08B")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        if(event.getClickedInventory() == bottomInv){

            //left
            if(slot == 18){
                customInventoryManager.dungeonLeft(player);
                openDungeonSelect(player);
                return;
            }

            //right
            if(slot == 26){
                customInventoryManager.dungeonRight(player);
                openDungeonSelect(player);
                return;
            }

            List<Integer> enterSlots = new ArrayList<>();
            enterSlots.add(0);
            enterSlots.add(1);
            enterSlots.add(2);

            if(enterSlots.contains(slot)){

                switch (customInventoryManager.getDungeonIndex(player)) {
                    case 0 -> {
                        Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Heart_of_Corruption");
                    }
                    case 1 -> {
                        Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Acolyte_of_Chaos");
                    }
                    case 2 -> {
                        Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Cave_of_Lindwyrm");
                    }
                    case 3 -> {
                        Mystica.dungeonsApi().initiateDungeonForPlayer(player, "Curse_of_Shadow");
                    }
                }

                return;
            }

            if(mysticaPartyManager.getMPartyLeader(player) != player){
                return;
            }

            List<Integer> matchSlots = new ArrayList<>();
            matchSlots.add(3);
            matchSlots.add(4);
            matchSlots.add(5);

            if(matchSlots.contains(slot)){


                return;
            }

            List<Integer> botSlots = new ArrayList<>();
            botSlots.add(5);
            botSlots.add(7);
            botSlots.add(8);

            if(botSlots.contains(slot)){
                matchMakingManager.fillWithBots(player);
                return;
            }

        }
    }

}
