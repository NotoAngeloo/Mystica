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

    public void openNewPlayerQuest(Player player){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("A Helping Hand");
        meta.setAuthor("");

        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "A Helping Hand" +
                ChatColor.RESET  +"\n\n" +
                "Now that you've completed your training, it's time to put it to the test in the real world. And who better to guide you on your first adventure than Captain Moon of the Hunter's Guild?");

        ComponentBuilder text2 = new ComponentBuilder("She's a seasoned veteran, respected by all who know her, and she's always on the lookout for capable recruits like yourself. Seek her out in her office.");

        ComponentBuilder text3 = new ComponentBuilder("Happy Hunting, and may your battles reap great reward!");




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

}
