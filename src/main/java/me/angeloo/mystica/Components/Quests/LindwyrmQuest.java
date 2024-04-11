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

        ComponentBuilder flavorText = new ComponentBuilder(ChatColor.UNDERLINE + "Cave of the Lindwyrm" +
                ChatColor.RESET  +"\n\n" +
                "A monster who never seems to die lives in the cave east of our town. " +
                "They may be minding their own business, but we need " + ChatColor.of(soulstoneColor) + "Soul Stones "+ ChatColor.RESET + "to create better equipment."+
                "\n\n");

        //the large blank space is to extend the hitbox of the unicode
        ComponentBuilder builder = new ComponentBuilder(ChatColor.WHITE +"\uE054" + "                                                                                       ")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/displaypath " + player.getName() + " 613 98 -89 please"));

        flavorText.append(builder.create());

        meta.spigot().addPage(flavorText.create());

        //this down here works, figure out how to make pages with it
        /*ComponentBuilder builder = new ComponentBuilder("test").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/classguide"));
        meta.spigot().addPage(builder.create());*/


        guide.setItemMeta(meta);
        player.openBook(guide);

    }
}
