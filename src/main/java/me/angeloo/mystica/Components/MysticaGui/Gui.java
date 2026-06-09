package me.angeloo.mystica.Components.MysticaGui;


import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPage;
import org.bukkit.entity.Player;

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
