package me.angeloo.mystica.Components.Quests;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class NewPlayerQuest {

    public NewPlayerQuest(){

    }

    public void openNewPlayerQuest(Player player, boolean reread){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("A New Hunter");
        meta.setAuthor("");


        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "A New Hunter" +
                ChatColor.RESET  +"\n\n" +
                "Welcome, newcomer. Since you have arrived through the portal, you must be a new hunter.\n\n" +
                "It is my job to teach new hunters the basic.");




        ComponentBuilder builder;
        if(!reread){
            builder = new ComponentBuilder(ChatColor.WHITE + "\uE054" + "                                                                                       ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " accept"));

        }
        else
        {
            builder = new ComponentBuilder(ChatColor.WHITE + "\uE055" + "                                                                                       ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " navigate"));

        }
        text.append(builder.create());
        //the large blank space is to extend the hitbox of the unicode


        meta.spigot().addPage(text.create());



        guide.setItemMeta(meta);
        player.openBook(guide);

    }

    public void openNewPlayerQuestComplete(Player player){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("A New Hunter");
        meta.setAuthor("");


        ComponentBuilder text = new ComponentBuilder("I think you are ready to start your journey.\n\n\n" +
                "Speak to me again if you want to change you fighting style.");



        meta.spigot().addPage(text.create());


        guide.setItemMeta(meta);
        player.openBook(guide);

    }

    public void openMissions(Player player, boolean reread){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("A New Hunter");
        meta.setAuthor("");


        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "A New Hunter" +
                ChatColor.RESET  +"\n\n" +
                "You must be itching for some action now.\n\n" +
                "Go see Captain Moon. She will have something for you to do.");




        ComponentBuilder builder;
        if(!reread){
            builder = new ComponentBuilder(ChatColor.WHITE + "\uE054" + "                                                                                       ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " accept"));

        }
        else
        {
            builder = new ComponentBuilder(ChatColor.WHITE + "\uE055" + "                                                                                       ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " navigate"));

        }
        text.append(builder.create());
        //the large blank space is to extend the hitbox of the unicode


        meta.spigot().addPage(text.create());



        guide.setItemMeta(meta);
        player.openBook(guide);

    }

}
