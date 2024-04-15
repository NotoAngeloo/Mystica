package me.angeloo.mystica.Components.Quests;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static me.angeloo.mystica.Mystica.soulstoneColor;

public class LindwyrmQuest {

    public LindwyrmQuest(){

    }

    public void openSewerQuest(Player player){

        ItemStack guide = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) guide.getItemMeta();
        assert meta != null;

        // Set the title and author of the book
        meta.setTitle("Cave of the Lindwyrm");
        meta.setAuthor("");

        ComponentBuilder text = new ComponentBuilder(ChatColor.UNDERLINE + "Cave of the Lindwyrm" +
                ChatColor.RESET  +"\n\n" +
                "There's a Lindwyrm lurking in a cave to the east, and it's been minding its own business for as long as anyone can remember. " +
                "Now, I know what you're thinking – why bother with a creature that's not causing any trouble?");

        ComponentBuilder text2 = new ComponentBuilder("Soul Stones, those little gems dropped by monsters when they're taken down, are valuable reagents for improving our gear. " +
                "Any Hunter could really use all they can get their hands on to keep their equipment in top shape.");

        ComponentBuilder text3 = new ComponentBuilder("If you're up for a bit of a challenge, you should head to the cave, slay the Lindwyrm, and bring back those Soul Stones. It's a little unethical, but in our line of work, you take every opportunity to gain strength.");

        ComponentBuilder text4 = new ComponentBuilder("What do you say, recruit? Ready to take on this challenge and reap the rewards?\n");

        //the large blank space is to extend the hitbox of the unicode
        ComponentBuilder builder = new ComponentBuilder(ChatColor.WHITE +"\uE054" + "                                                                                       ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mysticaquest " + player.getName() + " accept"));

        text4.append(builder.create());

        meta.spigot().addPage(text.create());
        meta.spigot().addPage(text2.create());
        meta.spigot().addPage(text3.create());
        meta.spigot().addPage(text4.create());

        //this down here works, figure out how to make pages with it
        /*ComponentBuilder builder = new ComponentBuilder("test").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/classguide"));
        meta.spigot().addPage(builder.create());*/


        guide.setItemMeta(meta);
        player.openBook(guide);

    }
}
