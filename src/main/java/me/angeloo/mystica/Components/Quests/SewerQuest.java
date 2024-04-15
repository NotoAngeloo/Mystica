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

        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "The Archbishop's Request" +
                ChatColor.RESET  +"\n\n" +
                "I've received word from the Archbishop himself, a rare occurrence indeed. It seems he's in need of someone with your particular skills for a matter of great importance.");

        ComponentBuilder text2 = new ComponentBuilder("The Archbishop's requests are not to be taken lightly. Whatever task he has in mind, it's bound to be significant");

        ComponentBuilder text3 = new ComponentBuilder("Head to the Cathedral and speak with him directly. " +
                "He'll provide you with the details of the assignment. And remember, if the Archbishop deems it important, it's likely to have far-reaching implications\n");




        //the large blank space is to extend the hitbox of the unicode
        ComponentBuilder builder = new ComponentBuilder(ChatColor.WHITE +"\uE054" + "                                                                                       ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " accept"));

        text3.append(builder.create());

        meta.spigot().addPage(text.create());
        meta.spigot().addPage(text2.create());
        meta.spigot().addPage(text3.create());


        guide.setItemMeta(meta);
        player.openBook(guide);

    }

    public void openSewerQuest2(Player player){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("Clean the Sewers");
        meta.setAuthor("");

        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "Heart of Corruption" +
                ChatColor.RESET  +"\n\n" +
                "Greetings, hunter. I am grateful for your swift response to our city's plight. There exists a darkness within our midst, a corruption that threatens to consume all that is pure and sacred.");

        ComponentBuilder text2 = new ComponentBuilder("Deep beneath the streets, in the murky depths of the sewers, lies the source of this malevolence: a heart of corruption, pulsating with unholy energy. " +
                "Its presence troubles me deeply, for I sense a connection, a weight upon my soul that I cannot ignore.");

        ComponentBuilder text3 = new ComponentBuilder("One thing is clear: this darkness must be confronted, lest it spread its taint further into our world.");

        ComponentBuilder text4 = new ComponentBuilder("I call upon you, hunter, to venture into the depths and confront this malevolent force. It is a task of great peril, but one that I trust you are capable of undertaking. ");

        ComponentBuilder text5 = new ComponentBuilder("Subdue this corruption from our city, before it spreads its corruption to those who dwell within its walls.\n");


        //the large blank space is to extend the hitbox of the unicode
        ComponentBuilder builder = new ComponentBuilder(ChatColor.WHITE +"\uE054" + "                                                                                       ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " accept"));

        text5.append(builder.create());


        meta.spigot().addPage(text.create());
        meta.spigot().addPage(text2.create());
        meta.spigot().addPage(text3.create());
        meta.spigot().addPage(text4.create());
        meta.spigot().addPage(text5.create());

        guide.setItemMeta(meta);
        player.openBook(guide);

    }

}
