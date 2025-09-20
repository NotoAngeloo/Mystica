package me.angeloo.mystica.Components.Guis.Party;

import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.Dungeon;
import me.angeloo.mystica.Utility.MatchMaking.MatchMakingManager;
import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
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
    private final DisplayWeapons displayWeapons;
    private final CustomInventoryManager customInventoryManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final MatchMakingManager matchMakingManager;

    public DungeonSelect(Mystica main){
        profileManager = main.getProfileManager();
        customInventoryManager = main.getInventoryManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        matchMakingManager = main.getMatchMakingManager();
        displayWeapons = main.getDisplayWeapons();
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
        player.getInventory().clear();
        displayWeapons.displayArmor(player);
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

                //TODO: if not enough players, notify and suggest bots
                switch (customInventoryManager.getDungeonIndex(player)) {
                    case 0 -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Heart_of_Corruption.name() + " " + player.getName());
                        player.closeInventory();
                    }
                    case 1 -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Acolyte_of_Chaos.name() + " " + player.getName());
                        player.closeInventory();
                    }
                    case 2 -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Cave_of_Lindwyrm.name() + " " + player.getName());
                        player.closeInventory();
                    }
                    case 3 -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "md play " + Dungeon.Curse_of_Shadow.name() + " " + player.getName());
                        player.closeInventory();
                    }
                }

                return;
            }

            if(mysticaPartyManager.getLeaderPlayer(player) != player){
                return;
            }

            List<Integer> matchSlots = new ArrayList<>();
            matchSlots.add(3);
            matchSlots.add(4);
            matchSlots.add(5);

            if(matchSlots.contains(slot)){
                matchMakingManager.matchMake(player);
                return;
            }

            List<Integer> botSlots = new ArrayList<>();
            botSlots.add(6);
            botSlots.add(7);
            botSlots.add(8);

            if(botSlots.contains(slot)){
                matchMakingManager.fillWithBots(player);
                return;
            }

        }
    }

}
