package me.angeloo.mystica.Components.Guis.Party;

import me.angeloo.mystica.Managers.Parties.MysticaPartyManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DisplayWeapons;
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
import java.util.List;

import static me.angeloo.mystica.Mystica.*;

public class InvitedInventory implements Listener {

    private final Mystica main;
    private final DisplayWeapons displayWeapons;
    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;

    public InvitedInventory(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        displayWeapons = main.getDisplayWeapons();
    }

    public void sendInviteInventory(Player player, Player inviter){

        String title = ChatColor.WHITE + "\uF807" + "\uE092";

        Inventory inv = Bukkit.createInventory(null, 9*6, title);

        inv.setItem(22, headGetter(inviter));

        player.openInventory(inv);
        player.getInventory().clear();
        displayWeapons.displayArmor(player);

    }

    private ItemStack headGetter(Player player){

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


        meta.setLore(lores);

        head.setItemMeta(meta);

        return head;
    }

    @EventHandler
    public void inviteClicks(InventoryClickEvent event){

        if(!event.getView().getTitle().contains("\uE092")){
            return;
        }

        event.setCancelled(true);


        Player player = (Player) event.getWhoClicked();

        Inventory topInv = event.getView().getTopInventory();

        ItemStack item = topInv.getItem(22);

        if(item == null){
            return;
        }

        if(!item.hasItemMeta()){
            return;
        }

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        assert meta != null;
        Player inviter = Bukkit.getPlayer(meta.getDisplayName());
        if(inviter == null){
            return;
        }

        Inventory bottomInv = event.getView().getBottomInventory();

        int slot = event.getSlot();

        if(event.getClickedInventory() == topInv){

            List<Integer> leftSlots = new ArrayList<>();
            leftSlots.add(45);
            leftSlots.add(46);
            leftSlots.add(47);

            if(leftSlots.contains(slot)){

                List<LivingEntity> mParty = mysticaPartyManager.getMysticaParty(inviter);

                if(mParty.size() >= 10){
                    player.closeInventory();
                    player.sendMessage("party is full");
                    return;
                }

                if(mysticaPartyManager.inPParty(player)){
                    //check size
                    return;
                }

                player.closeInventory();

                Bukkit.getScheduler().runTask(main, () -> mysticaPartyManager.joinParty(player, inviter));


                return;
            }

            List<Integer> rightSlots = new ArrayList<>();
            rightSlots.add(51);
            rightSlots.add(52);
            rightSlots.add(53);

            if(rightSlots.contains(slot)){
                player.closeInventory();

                //and send denial message, also do this if this closes
            }

        }
    }

}
