package me.angeloo.mystica.Components.MysticaGui;


import me.angeloo.mystica.Components.MysticaGui.Guis.Pages.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class Gui {


    /*
     * -----------------------------------------
     * Lifecycle
     * -----------------------------------------
     */

    public void onOpen(Player player) {

    }

    public void onClose(Player player) {

    }

    /*
     * -----------------------------------------
     * Pages
     * -----------------------------------------
     */

    public abstract GuiPage getInitialPage();


}
