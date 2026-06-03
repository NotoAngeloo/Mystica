package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Components.MysticaGui.Guis.Pages.GuiPage;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager{

    private final Map<UUID, GuiSession> sessions = new HashMap<>();

    private final GuiRenderer renderer;

    public GuiManager(
            GuiRenderer renderer
    ) {

        this.renderer = renderer;
    }

    /*
     * -----------------------------------------
     * Open
     * -----------------------------------------
     */

    public void open(
            Player player,
            Gui gui
    ) {

        close(player);

        /*
         * Resolve initial page
         */

        GuiPage initialPage = gui.getInitialPage();

        /*
         * Create session FIRST
         */

        GuiSession session =
                new GuiSession(
                        gui,
                        initialPage
                );

        /*
         * Register session
         */

        sessions.put(
                player.getUniqueId(),
                session
        );

        /*
         * Render GUI
         */

        GuiRenderResult result =
                renderer.render(
                        player,
                        session
                );

        /*
         * Create inventory using
         * rendered title
         */

        Inventory inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.WHITE + result.title()
                );

        /*
         * Store inventory
         */

        session.setInventory(
                inventory
        );

        /*
         * Cache render
         */

        session.setLastRender(result);

        session.clearDirty();

        /*
         * Lifecycle
         */

        gui.onOpen(player);

        /*
         * Open inventory
         */

        player.openInventory(
                inventory
        );
    }

    /*
     * -----------------------------------------
     * Rendering
     * -----------------------------------------
     */


    public void refresh(
            Player player
    ) {

        GuiSession session = getSession(player);

        if(session == null)
            return;


        GuiRenderResult result = renderer.render(player, session);

        session.setLastRender(result);
        session.clearDirty();

        InventoryView view = player.getOpenInventory();

        view.setTitle(ChatColor.WHITE + result.title());

        /*Inventory inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.WHITE + result.title()
                );

        session.setInventory(
                inventory
        );*/


        //no on open method

        /*player.openInventory(
                inventory
        );*/
    }

    /*
     * -----------------------------------------
     * Close
     * -----------------------------------------
     */

    public void close(Player player) {


        GuiSession session =
                sessions.remove(
                        player.getUniqueId()
                );

        if(session == null)
            return;

        session.setClosing(true);

        session.getGui().onClose(player);
    }

    /*
     * -----------------------------------------
     * Sessions
     * -----------------------------------------
     */

    public GuiSession getSession(
            Player player
    ) {

        return sessions.get(
                player.getUniqueId()
        );
    }


    public void handleClick(Player player, InventoryClickEvent event) {

        GuiSession session = getSession(player);

        //problem, session becomes null after page change

        if(session == null)
            return;


        session.getCurrentPage().handleClick(player, event);
    }

    public void handleDrag(Player player, InventoryDragEvent event) {

        GuiSession session = getSession(player);

        if(session == null)
            return;

        session.getCurrentPage()
                .handleDrag(player, event);
    }

    public void handleClose(
            Player player,
            InventoryCloseEvent event
    ) {

        GuiSession session = getSession(player);

        if(session == null)
            return;

        /*
         * Mark closing to prevent
         * re-entrancy issues (optional but good)
         */

        session.setClosing(true);

        /*
         * Page-level lifecycle
         */

        session.getCurrentPage().handleClose(player, event);

        /*
         * Root GUI lifecycle
         */

        session.getGui().onClose(player);

        /*
         * Remove session AFTER hooks
         */

        sessions.remove(
                player.getUniqueId()
        );
    }

}
