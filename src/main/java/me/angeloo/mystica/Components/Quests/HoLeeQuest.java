package me.angeloo.mystica.Components.Quests;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class HoLeeQuest {

    public HoLeeQuest(){

    }

    public void openHoLeeQuest(Player player, boolean reread){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("The General's Arrival");
        meta.setAuthor("");

        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "The General's Arrival" +
                ChatColor.RESET  +"\n\n" +
                "Hey there, hunter. Got a bit of a task for you. Seems we've got a visitor on the outskirts of town, goes by the name of Ho Lee. " +
                "He's a general from the neighboring nation, here for... well, we're not quite sure why he's here");

        ComponentBuilder text2 = new ComponentBuilder("Ho Lee always refuses to come into town. I'm sure he has his reasons, but I've never bothered to ask. " +
                "In any case, I'd like to determine what business brings him to our shores.");

        ComponentBuilder text3 = new ComponentBuilder("I need you to head out there and greet him. Ho Lee can be a bit impatient, so it's best not to keep him waiting too long. " +
                "Maybe he's got some info to share, or maybe he's just passing through. Either way, we need to find out what's brought him here.");

        ComponentBuilder text4 = new ComponentBuilder("Off you go now, hunter. Don't keep our guest waiting.\n");

        ComponentBuilder builder;
        if(!reread){
            //the large blank space is to extend the hitbox of the unicode
            builder = new ComponentBuilder(ChatColor.WHITE + "\uE054" + "                                                                                       ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " accept"));

        }
        else {
            builder = new ComponentBuilder(ChatColor.WHITE + "\uE055" + "                                                                                       ")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " navigate"));

        }
        text4.append(builder.create());

        meta.spigot().addPage(text.create());
        meta.spigot().addPage(text2.create());
        meta.spigot().addPage(text3.create());
        meta.spigot().addPage(text4.create());

        guide.setItemMeta(meta);
        player.openBook(guide);


    }

}
