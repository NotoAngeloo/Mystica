package me.angeloo.mystica.Components.Quests;

import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;



public class SewerQuest {


    public SewerQuest(){
    }

    public void openSewerQuest(Player player){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("Clean the Sewers");
        meta.setAuthor("");

        ComponentBuilder flavorText = new ComponentBuilder("  " + ChatColor.UNDERLINE + "Cleaning The Sewer" +
                ChatColor.RESET  +"\n\n" +
                "There have been many complaints about some thumping sounds coming from the sewer. " +
                "The archbishop especially because he cannot focus on resurrecting hunters. ");

        ComponentBuilder flavorText2 = new ComponentBuilder("In any case, I would like you to investigate." +
                "\n\n");

        //the large blank space is to extend the hitbox of the unicode
        ComponentBuilder builder = new ComponentBuilder(ChatColor.WHITE +"\uE054" + "                                                                                       ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/displaypath " + player.getName() + " 57 99 -292 please"));

        flavorText2.append(builder.create());

        meta.spigot().addPage(flavorText.create());
        meta.spigot().addPage(flavorText2.create());

        //this down here works, figure out how to make pages with it
        /*ComponentBuilder builder = new ComponentBuilder("test").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/classguide"));
        meta.spigot().addPage(builder.create());*/


        guide.setItemMeta(meta);
        player.openBook(guide);

    }

}
