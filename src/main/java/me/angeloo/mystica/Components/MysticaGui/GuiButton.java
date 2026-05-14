package me.angeloo.mystica.Components.MysticaGui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface GuiButton {

    void click(Player player, Gui gui, InventoryClickEvent event);

}
