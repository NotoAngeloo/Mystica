package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public interface GuiButton {

    /*
     * Top-left render origin
     */

    int slot();

    /*
     * Interactive region
     */

    Set<Integer> interactionSlots();

    /*
     * Visual glyph
     */

    Glyph glyph();

    void click(
            Player player,
            Gui gui,
            InventoryClickEvent event
    );

}
