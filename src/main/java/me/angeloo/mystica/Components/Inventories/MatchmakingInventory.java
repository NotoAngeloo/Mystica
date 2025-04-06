package me.angeloo.mystica.Components.Inventories;

import me.angeloo.mystica.Managers.MatchMakingManager;
import me.angeloo.mystica.Managers.MysticaPartyManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.MysticaParty;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MatchmakingInventory implements Listener {

    private final MysticaPartyManager mysticaPartyManager;
    private final MatchMakingManager matchMakingManager;

    public MatchmakingInventory(Mystica main, MatchMakingManager manager){
        mysticaPartyManager = main.getMysticaPartyManager();
        matchMakingManager = manager;
    }

    public Inventory openDungeonEnter(String dungeon){

        String dungeonSplash = "";

        switch (dungeon.toLowerCase()){
            case "cave_of_lindwyrm":{
                dungeonSplash = "\uE08E";
                break;
            }
            case "curse_of_shadow":{
                dungeonSplash = "\uE08F";
                break;
            }
            case "heart_of_corruption":{
                dungeonSplash = "\uE090";
                break;
            }
            case "acolyte_of_chaos":{
                dungeonSplash = "\uE091";
                break;
            }
            default:{
                Bukkit.getLogger().info("invalid dungeon");
                break;
            }


        }

        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF807" + dungeonSplash + "\uF80D" + "\uF82B\uF829" + "\uE08B");

        //inv.setItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        //inv.setItem(53, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

        return inv;
    }

    public Inventory openMatchFound(String dungeon, List<Player> joiners, boolean bots){
        String dungeonSplash = "";

        switch (dungeon.toLowerCase()){
            case "cave_of_lindwyrm":{
                dungeonSplash = "\uE08E";
                break;
            }
            case "curse_of_shadow":{
                dungeonSplash = "\uE08F";
                break;
            }
            case "heart_of_corruption":{
                dungeonSplash = "\uE090";
                break;
            }
            case "acolyte_of_chaos":{
                dungeonSplash = "\uE091";
                break;
            }
            default:{
                Bukkit.getLogger().info("invalid dungeon");
                break;
            }


        }

        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF807" + dungeonSplash + "\uF80D" + "\uF82B\uF829" + "\uE08D");

        //default bot icons
        for(int i = 18; i<=26;i+=2){
            inv.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        }

        //fill the slots with the joiners
        int itemSlot = 18;
        for(int i = 0; i<=joiners.size()-1; i++){
            inv.setItem(itemSlot, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            itemSlot += 2;
        }

        

        if(bots){
            inv.setItem(8, getItem(Material.KELP, 1, " ",
                    "this item helps the gui work"));
        }

        return inv;
    }

    public Inventory openRoleSelect(String dungeon, boolean bots){
        String dungeonSplash = "";

        switch (dungeon.toLowerCase()) {
            case "cave_of_lindwyrm": {
                dungeonSplash = "\uE08E";
                break;
            }
            case "curse_of_shadow": {
                dungeonSplash = "\uE08F";
                break;
            }
            case "heart_of_corruption": {
                dungeonSplash = "\uE090";
                break;
            }
            case "acolyte_of_chaos": {
                dungeonSplash = "\uE091";
                break;
            }
            default: {
                Bukkit.getLogger().info("invalid dungeon");
                break;
            }
        }

        Inventory inv = Bukkit.createInventory(null, 9 * 6, ChatColor.WHITE + "\uF807" + dungeonSplash + "\uF80D" + "\uF82B\uF829" + "\uE092");

        if(bots){
            inv.setItem(53, getItem(Material.KELP, 1, " ",
                    "this item helps the gui work"));
        }

        return inv;
    }

    @EventHandler
    public void dungeonEnterClicks(InventoryClickEvent event){

        //This is the main page, in which you select to join the dungeon, matchmake, or instant bots
        if(event.getView().getTitle().contains("\uE08B")){

            event.setCancelled(true);

            Inventory inv = event.getView().getTopInventory();

            if(event.getClickedInventory() != inv){
                return;
            }

            String title = event.getView().getTitle();

            String dungeon = "";

            if(title.contains("\uE08E")){
                dungeon = "Cave_of_Lindwyrm";
            }

            if(title.contains("\uE08F")){
                dungeon = "Curse_of_Shadow";
            }

            if(title.contains("\uE090")){
                dungeon = "Heart_of_Corruption";
            }

            if(title.contains("\uE091")){
                dungeon = "Acolyte_of_Chaos";
            }

            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();

            List<Integer> enterSlots = new ArrayList<>();
            enterSlots.add(45);
            enterSlots.add(46);
            enterSlots.add(47);

            if(enterSlots.contains(slot)){
                //maybe throw a warning if doesn't have max number of players
                Mystica.dungeonsApi().initiateDungeonForPlayer(player, dungeon);
                return;
            }

            List<Integer> matchmakerSlots = new ArrayList<>();
            matchmakerSlots.add(48);
            matchmakerSlots.add(49);
            matchmakerSlots.add(50);

            if(matchmakerSlots.contains(slot)){

                if(mysticaPartyManager.getPlayerMParty(player) == null){
                    Bukkit.getLogger().info("creating mparty to matchmake");
                    Player leader = mysticaPartyManager.getMPartyLeader(player);
                    mysticaPartyManager.createMParty(leader);
                }

                mysticaPartyManager.getPlayerMParty(player).clearRoles();

                for(Player partyPlayer : mysticaPartyManager.getPartyPlayers(player)){
                    partyPlayer.openInventory(openRoleSelect(dungeon, false));
                }

                return;
            }

            List<Integer> botSlots = new ArrayList<>();
            botSlots.add(51);
            botSlots.add(52);
            botSlots.add(53);

            if(botSlots.contains(slot)){

                //get for all mparty
                if(mysticaPartyManager.getPlayerMParty(player) == null){
                    Bukkit.getLogger().info("creating mparty for bots");
                    Player leader = mysticaPartyManager.getMPartyLeader(player);
                    mysticaPartyManager.createMParty(leader);
                }

                mysticaPartyManager.getPlayerMParty(player).clearRoles();

                for(Player partyPlayer : mysticaPartyManager.getPartyPlayers(player)){
                    partyPlayer.openInventory(openRoleSelect(dungeon, true));
                }

                return;
            }

        }

        //this is the matchmaking page
        if(event.getView().getTitle().contains("\uE092")){


            event.setCancelled(true);

            Inventory inv = event.getView().getTopInventory();

            if(event.getClickedInventory() != inv){
                return;
            }

            boolean bots = inv.getItem(53) != null;

            String title = event.getView().getTitle();

            String dungeon = "";

            if(title.contains("\uE08E")){
                dungeon = "Cave_of_Lindwyrm";
            }

            if(title.contains("\uE08F")){
                dungeon = "Curse_of_Shadow";
            }

            if(title.contains("\uE090")){
                dungeon = "Heart_of_Corruption";
            }

            if(title.contains("\uE091")){
                dungeon = "Acolyte_of_Chaos";
            }

            Player player = (Player) event.getWhoClicked();

            MysticaParty mParty = mysticaPartyManager.getPlayerMParty(player);

            int slot = event.getSlot();

            List<Integer> dpsSlots = new ArrayList<>();
            dpsSlots.add(9);
            dpsSlots.add(10);
            dpsSlots.add(11);
            dpsSlots.add(18);
            dpsSlots.add(19);
            dpsSlots.add(20);
            dpsSlots.add(27);
            dpsSlots.add(28);
            dpsSlots.add(29);

            List<Integer> tankSlots = new ArrayList<>();
            tankSlots.add(12);
            tankSlots.add(13);
            tankSlots.add(14);
            tankSlots.add(21);
            tankSlots.add(22);
            tankSlots.add(23);
            tankSlots.add(30);
            tankSlots.add(31);
            tankSlots.add(32);

            List<Integer> healSlots = new ArrayList<>();
            healSlots.add(15);
            healSlots.add(16);
            healSlots.add(17);
            healSlots.add(24);
            healSlots.add(25);
            healSlots.add(26);
            healSlots.add(33);
            healSlots.add(34);
            healSlots.add(35);

            boolean soloQueue = false;
            if(!bots){
                if(mysticaPartyManager.getPartyPlayers(player).size()==1){
                    soloQueue = true;
                }
            }


            if(dpsSlots.contains(slot)){

                if(soloQueue){
                    matchMakingManager.queueDamage(player, dungeon);
                    player.closeInventory();
                    return;
                }

                mParty.addOrChangeMemberRole(player, "damage");
                player.closeInventory();
                matchMakingManager.matchMakeReadyCheck(player, dungeon, bots);
                return;
            }

            if(tankSlots.contains(slot)){

                if(soloQueue){
                    matchMakingManager.queueTank(player, dungeon);
                    player.closeInventory();
                    return;
                }

                mParty.addOrChangeMemberRole(player, "tank");
                player.closeInventory();
                matchMakingManager.matchMakeReadyCheck(player, dungeon, bots);
                return;
            }

            if(healSlots.contains(slot)){

                if(soloQueue){
                    matchMakingManager.queueHeal(player, dungeon);
                    player.closeInventory();
                    return;
                }

                mParty.addOrChangeMemberRole(player, "heal");
                player.closeInventory();
                matchMakingManager.matchMakeReadyCheck(player, dungeon, bots);
                return;
            }

            return;
        }

        if(event.getView().getTitle().contains("\uE08D")) {


            event.setCancelled(true);

            Inventory inv = event.getView().getTopInventory();

            if (event.getClickedInventory() != inv) {
                return;
            }

            boolean bots = inv.getItem(8) != null;

            String title = event.getView().getTitle();

            String dungeon = "";

            if (title.contains("\uE08E")) {
                dungeon = "Cave_of_Lindwyrm";
            }

            if (title.contains("\uE08F")) {
                dungeon = "Curse_of_Shadow";
            }

            if (title.contains("\uE090")) {
                dungeon = "Heart_of_Corruption";
            }

            if (title.contains("\uE091")) {
                dungeon = "Acolyte_of_Chaos";
            }

            Player player = (Player) event.getWhoClicked();

            int slot = event.getSlot();

            List<Integer> confirmSlots = new ArrayList<>();
            confirmSlots.add(45);
            confirmSlots.add(46);
            confirmSlots.add(47);

            List<Integer> cancelSlots = new ArrayList<>();
            cancelSlots.add(51);
            cancelSlots.add(52);
            cancelSlots.add(53);


            if(confirmSlots.contains(slot)){
                //perhaps I can have another inventory that shows number of people?? or just grey out the confirm button
                player.closeInventory();
                matchMakingManager.matchMakeConfirmEnter(player, dungeon, bots);
                return;
            }

            if(cancelSlots.contains(slot)){
                player.closeInventory();
                matchMakingManager.cancelEnterDungeon(player);
                return;
            }
        }

    }

    private ItemStack getItem(Material material, int modelData, String name, String ... lore) {

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setUnbreakable(true);

        List<String> lores = new ArrayList<>();

        for (String s : lore) {
            lores.add(ChatColor.translateAlternateColorCodes('&', s));
        }

        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(lores);
        meta.setCustomModelData(modelData);

        item.setItemMeta(meta);
        return item;
    }

}
