package me.angeloo.mystica.Components.Guis.Party;

import me.angeloo.mystica.Components.Guis.QuestInventories.QuestAcceptInventory;
import me.angeloo.mystica.Components.Guis.InventoryTextGenerator;
import me.angeloo.mystica.Components.Guis.CustomInventoryManager;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.Dungeon;
import me.angeloo.mystica.Utility.MatchMaking.MatchMakingManager;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;

public class DungeonSelect implements Listener {

    private final ProfileManager profileManager;
    private final InventoryTextGenerator textGenerator;
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
        textGenerator = customInventoryManager.getTextGenerator();
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

        Player leaderPlayer = mysticaPartyManager.getLeaderPlayer(player);
        int bossLevel = profileManager.getAnyProfile(leaderPlayer).getPlayerBossLevel().getBossLevel();

        String dungeonLevelText = textGenerator.getDungeonLevelText(bossLevel);

        //-9, dungeon, -256, +75, bottom gui, -256, +96, level
        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF808\uF801" + dungeonSplash + "\uF80D" + "\uF82B\uF828\uF823" + "\uE08B" + "\uF80D" + "\uF82B\uF82A" + dungeonLevelText);

        //inv.setItem(45, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(53, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));


        /*inv.setItem(37, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(38, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(39, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        inv.setItem(41, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(42, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(43, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));*/


        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        /*player.getInventory().setItem(9, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(17, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(18, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(22, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        player.getInventory().setItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        player.getInventory().setItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));*/
    }

    private void openDungeonNotice(Player player){

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

        //-9, dungeon, -256, +73, notice
        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF808\uF801" + dungeonSplash + "\uF80D" + "\uF82B\uF828\uF821" + "\uE062");

        /*inv.setItem(37, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(38, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(39, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        inv.setItem(41, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(42, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        inv.setItem(43, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));*/


        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);
    }

    @EventHandler
    public void noticeClicks(InventoryClickEvent event){
        //the notice gui
        if(!event.getView().getTitle().contains("\uE062")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();

        Inventory topInv = event.getView().getTopInventory();

        if(event.getClickedInventory() == topInv){
            Set<Integer> enterSlots = new HashSet<>();
            enterSlots.add(37);
            enterSlots.add(38);
            enterSlots.add(39);

            if(enterSlots.contains(slot)){

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

            Set<Integer> botSlots = new HashSet<>();
            botSlots.add(41);
            botSlots.add(42);
            botSlots.add(43);

            if(botSlots.contains(slot)){
                matchMakingManager.fillWithBots(player);
                return;
            }
        }
    }

    @EventHandler
    public void dungeonClicks(InventoryClickEvent event){

        //the bottom gui
        if(!event.getView().getTitle().contains("\uE08B")){
            return;
        }

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();
        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        if(event.getClickedInventory() == topInv){

            if(slot == 45){
                customInventoryManager.dungeonLeft(player);
                openDungeonSelect(player);
                return;
            }

            if(slot == 53){
                customInventoryManager.dungeonRight(player);
                openDungeonSelect(player);
                return;
            }

            return;
        }

        if(event.getClickedInventory() == bottomInv){

            if(mysticaPartyManager.getLeaderPlayer(player) != player){
                return;
            }

            if(slot == 18){
                profileManager.getAnyProfile(player).getPlayerBossLevel().decrease();
                openDungeonSelect(player);
                return;
            }

            if(slot == 22){
                profileManager.getAnyProfile(player).getPlayerBossLevel().increase();
                openDungeonSelect(player);
                return;
            }

            Set<Integer> enterSlots = new HashSet<>();
            enterSlots.add(0);
            enterSlots.add(1);
            enterSlots.add(2);

            if(enterSlots.contains(slot)){

                //if not enough players, notify and suggest bots
                if(mysticaPartyManager.getMysticaParty(player).size()<5){
                    openDungeonNotice(player);
                    return;
                }



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

            Set<Integer> matchSlots = new HashSet<>();
            matchSlots.add(6);
            matchSlots.add(7);
            matchSlots.add(8);

            if(matchSlots.contains(slot)){
                player.sendMessage("coming soon");
                //matchMakingManager.matchMake(player);
                return;
            }

        }

    }


}
