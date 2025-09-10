package me.angeloo.mystica.Components.Guis.Party;

import me.angeloo.mystica.Managers.CustomInventoryManager;
import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.Enums.Role;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static me.angeloo.mystica.Mystica.*;

public class PartyInventory implements Listener {

    private final ProfileManager profileManager;
    private final DisplayWeapons displayWeapons;
    private final MysticaPartyManager partyManager;
    private final CustomInventoryManager inventoryManager;
    private final InvitedInventory invitedInventory;


    public PartyInventory(Mystica main){
        profileManager = main.getProfileManager();
        partyManager = main.getMysticaPartyManager();
        inventoryManager = main.getInventoryManager();
        displayWeapons = main.getDisplayWeapons();
        invitedInventory = main.getInvitedInventory();
    }

    public void openPartyInventory(Player player){

        int index = inventoryManager.getPartyIndex(player);

        String sortByPng;

        switch (index) {
            case 0 -> {
                //all
                sortByPng = "\uE05E";
            }
            case 1 -> {
                //near
                sortByPng = "\uE05F";
            }
            case 2 -> {
                //friends
                sortByPng = "\uE060";
            }
            default -> {
                return;
            }
        }

        String teamSquadPng = "\uE08C";

        if(partyManager.getMysticaParty(player).size() > 5){
            teamSquadPng = "\uE08D";
        }

        if(!partyManager.inPParty(player)){
            teamSquadPng = "";
        }

        //-7, png, +8, -265, +78, png
        String title = ChatColor.WHITE + "\uF807" + sortByPng + "\uF828" + "\uF80D" + "\uF82B\uF828\uF826" + teamSquadPng;
        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<LivingEntity> mParty = partyManager.getMysticaParty(player);

        Role filter = inventoryManager.getRoleFilter(player);

        switch (index) {
            case 0 -> {

                int slot = 0;
                for (Player online : onlinePlayers) {

                    if (player == online) {
                        continue;
                    }

                    if (mParty.contains(online)) {
                        continue;
                    }

                    if(partyManager.inPParty(online)){

                        //check if party can merge, later

                        continue;
                    }

                    if (slot >= 45) {
                        break;
                    }

                    //check role filter
                    if(!filter.equals(Role.None)){
                        if (!inventoryManager.getRole(online).equals(filter)) {
                            continue;
                        }
                    }



                    inv.setItem(slot, inviteHead(online));
                    slot++;
                }

            }
            case 1 -> {

                int slot = 0;
                onlinePlayers.sort(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(player.getLocation())));
                for (Player online : onlinePlayers) {

                    if (player == online) {
                        continue;
                    }

                    if (mParty.contains(online)) {
                        continue;
                    }

                    if (slot >= 45) {
                        break;
                    }

                    //check role filter
                    if (!inventoryManager.getRole(online).equals(filter)) {
                        continue;
                    }


                    inv.setItem(slot, inviteHead(online));
                    slot++;
                }

            }
            case 2 -> {

                //friends not implemented yet
            }
        }

        player.openInventory(inv);

        player.getInventory().clear();
        displayWeapons.displayArmor(player);

        if(!partyManager.inPParty(player)){
            return;
        }

        //Player leaderPlayer = partyManager.getLeaderPlayer(player);
        Player leaderPlayer = partyManager.getLeaderPlayer(player);

        player.getInventory().setItem(13, teamHead(leaderPlayer, player));

        //depending on size, put members in places of inventory

        if(mParty.size() > 5){

            int slot = 18;
            for(LivingEntity member : mParty){

                if(member == leaderPlayer){
                    continue;
                }

                if(member instanceof Player mPlayer){
                    player.getInventory().setItem(slot, teamHead(mPlayer, player));

                }

                slot++;
            }

        }
        else{

            int slot = 19;
            for(LivingEntity member : mParty){

                if(member == leaderPlayer){
                    continue;
                }

                if(member instanceof Player mPlayer){
                    player.getInventory().setItem(slot, teamHead(mPlayer, player));

                }

                slot+=2;
            }
        }


    }

    private ItemStack inviteHead(Player player){

        player = Bukkit.getOfflinePlayer(player.getUniqueId()).getPlayer();
        assert player != null;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;

        meta.setOwningPlayer(player);

        meta.setDisplayName(player.getName());

        List<String> lores = new ArrayList<>();

        Color classColor = Color.WHITE;

        switch (profileManager.getAnyProfile(player).getPlayerClass()){

            case Ranger -> {
                classColor = rangerColor;
            }
            case Warrior ->{
                classColor = warriorColor;
            }
            case Paladin -> {
                classColor = paladinColor;
            }
            case Shadow_Knight -> {
                classColor = shadowKnightColor;
            }
            case Elementalist -> {
                classColor = elementalistColor;
            }
            case Mystic -> {
                classColor = mysticColor;
            }
            case Assassin -> {
                classColor = assassinColor;
            }

        }

        lores.add(ChatColor.of(classColor) + profileManager.getAnyProfile(player).getPlayerClass().name());

        lores.add(ChatColor.of(menuColor) + "Role: " + inventoryManager.getRole(player));

        lores.add("");
        lores.add(ChatColor.of(menuColor) + "Click to invite");


        meta.setLore(lores);

        head.setItemMeta(meta);

        return head;
    }

    private ItemStack teamHead(Player player, Player mePlayer){

        Player leaderPlayer = partyManager.getLeaderPlayer(player);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;

        meta.setOwningPlayer(player);

        meta.setDisplayName(player.getName());

        List<String> lores = new ArrayList<>();

        Color classColor = Color.WHITE;

        switch (profileManager.getAnyProfile(player).getPlayerClass()){

            case Ranger -> {
                classColor = rangerColor;
            }
            case Warrior ->{
                classColor = warriorColor;
            }
            case Paladin -> {
                classColor = paladinColor;
            }
            case Shadow_Knight -> {
                classColor = shadowKnightColor;
            }
            case Elementalist -> {
                classColor = elementalistColor;
            }
            case Mystic -> {
                classColor = mysticColor;
            }
            case Assassin -> {
                classColor = assassinColor;
            }

        }

        lores.add(ChatColor.of(classColor) + profileManager.getAnyProfile(player).getPlayerClass().name());

        lores.add(ChatColor.of(menuColor) + "Role: " + inventoryManager.getRole(player));

        if(player.getUniqueId() == mePlayer.getUniqueId()){
            lores.add("");
            lores.add(ChatColor.of(menuColor) + "Click to leave");
            meta.setLore(lores);
            head.setItemMeta(meta);
            return head;
        }

        if(leaderPlayer.getUniqueId() == mePlayer.getUniqueId()){
            lores.add("");
            lores.add(ChatColor.of(menuColor) + "Click to remove");
        }




        meta.setLore(lores);
        head.setItemMeta(meta);
        return head;
    }

    @EventHandler
    public void partyClicks(InventoryClickEvent event){

        if(event.getView().getTitle().contains("\uE05E") ||
            event.getView().getTitle().contains("\uE05F") ||
            event.getView().getTitle().contains("\uE060")){
            event.setCancelled(true);



            Player player = (Player) event.getWhoClicked();

            Inventory topInv = event.getView().getTopInventory();
            Inventory bottomInv = event.getView().getBottomInventory();

            int slot = event.getSlot();

            if(event.getClickedInventory() == topInv){

                Role filter = inventoryManager.getRoleFilter(player);

                if(slot == 51){

                    if(filter != Role.Tank){
                        inventoryManager.setRoleFilter(player, Role.Tank);
                    }
                    else{
                        inventoryManager.setRoleFilter(player, Role.None);
                    }

                    openPartyInventory(player);
                    return;
                }

                if(slot == 52){
                    if(filter != Role.Healer){
                        inventoryManager.setRoleFilter(player, Role.Healer);
                    }
                    else{
                        inventoryManager.setRoleFilter(player, Role.None);
                    }

                    openPartyInventory(player);
                    return;
                }

                if(slot == 53){
                    if(filter != Role.Damage){
                        inventoryManager.setRoleFilter(player, Role.Damage);
                    }
                    else{
                        inventoryManager.setRoleFilter(player, Role.None);
                    }

                    openPartyInventory(player);
                    return;
                }

                List<Integer> leftSlots = new ArrayList<>();
                leftSlots.add(45);
                leftSlots.add(46);
                leftSlots.add(47);

                List<Integer> midSlots = new ArrayList<>();
                midSlots.add(48);
                midSlots.add(49);
                midSlots.add(50);

                if(event.getView().getTitle().contains("\uE05E")){

                    if(leftSlots.contains(slot)){
                        //distance
                        inventoryManager.setPartyIndex(player, 1);
                        openPartyInventory(player);
                        return;
                    }

                    if(midSlots.contains(slot)){
                        //friends
                        inventoryManager.setPartyIndex(player, 2);
                        openPartyInventory(player);
                        return;
                    }
                }

                if(event.getView().getTitle().contains("\uE05F")){

                    if(leftSlots.contains(slot)){
                        //all
                        inventoryManager.setPartyIndex(player, 0);
                        openPartyInventory(player);
                        return;
                    }

                    if(midSlots.contains(slot)){
                        //friends
                        inventoryManager.setPartyIndex(player, 2);
                        openPartyInventory(player);
                        return;
                    }

                }

                if(event.getView().getTitle().contains("\uE060")){

                    if(leftSlots.contains(slot)){
                        //all
                        inventoryManager.setPartyIndex(player, 0);
                        openPartyInventory(player);
                        return;
                    }

                    if(midSlots.contains(slot)){
                        //distance
                        inventoryManager.setPartyIndex(player, 1);
                        openPartyInventory(player);
                        return;
                    }

                }

                ItemStack item = event.getCurrentItem();

                if(item == null){
                    return;
                }

                if(!item.hasItemMeta()){
                    return;
                }

                SkullMeta meta = (SkullMeta) item.getItemMeta();
                assert meta != null;
                Player invitePlayer = Bukkit.getPlayer(meta.getDisplayName());

                assert invitePlayer != null;
                invitedInventory.sendInviteInventory(invitePlayer, player);


                return;
            }

            if(event.getClickedInventory() == bottomInv){

                ItemStack item = event.getCurrentItem();

                if(item == null){
                    return;
                }

                if(!item.hasItemMeta()){
                    return;
                }

                SkullMeta meta = (SkullMeta) item.getItemMeta();
                assert meta != null;
                Player pPlayer = Bukkit.getPlayer(meta.getDisplayName());
                assert pPlayer != null;

                partyManager.removeFromParty(pPlayer);
                openPartyInventory(player);
            }

        }

    }

}
