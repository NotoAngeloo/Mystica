package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {

    private final Map<UUID, GuiSession> sessions =
            new HashMap<>();

    private final GuiRenderer renderer;

    public GuiManager(
            GuiRenderer renderer
    ) {

        this.renderer = renderer;
    }

    /*
     * -----------------------------------------
     * GUI Lifecycle
     * -----------------------------------------
     */

    public void open(
            Player player,
            Gui gui
    ) {

        close(player);

        GuiRenderResult result =
                renderer.render(
                        player,
                        gui
                );

        Inventory inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.WHITE + result.title()
                );

        GuiSession session =
                new GuiSession(
                        gui,
                        inventory
                );

        session.setLastRender(result);

        sessions.put(
                player.getUniqueId(),
                session
        );

        gui.onOpen(player);

        player.openInventory(inventory);
    }

    public void close(Player player) {

        GuiSession session = sessions.remove(player.getUniqueId());

        if(session == null){
            return;
        }

        Gui gui = session.getGui();

        if(gui == null){
            return;
        }

        gui.onClose(player);

        player.closeInventory();
    }

    /*
     * -----------------------------------------
     * Access
     * -----------------------------------------
     */

    public GuiSession get(Player player) {

        return sessions.get(
                player.getUniqueId()
        );
    }

    public boolean hasGui(Player player) {

        return sessions.containsKey(
                player.getUniqueId()
        );
    }

    /*
     * -----------------------------------------
     * Rendering
     * -----------------------------------------
     */

    public void render(Player player) {

        GuiSession session =
                sessions.get(player.getUniqueId());

        if (session == null)
            return;

        if (!session.isDirty())
            return;

        Gui gui = session.getGui();

        GuiRenderResult result =
                renderer.render(player, gui);

        updateTitle(player, result.title());

        session.setLastRender(result);

        session.clearDirty();
    }

    private void updateTitle(
            Player player,
            String title
    ) {

        /*
         * Packet inventory title update.
         */
    }

    /*
     * -----------------------------------------
     * Inventory Opening
     * -----------------------------------------
     */

    private void openInventory(
            Player player,
            GuiRenderResult result
    ) {

        /*
         * TEMPORARY IMPLEMENTATION
         *
         * Later:
         * inventory reuse
         * packet inventories
         * title updates
         * partial rerenders
         */

        Inventory inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.WHITE + result.title()
                );

        player.openInventory(inventory);
    }

    /*
     * -----------------------------------------
     * Input Events
     * -----------------------------------------
     */

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        GuiSession session = sessions.get(player.getUniqueId());

        if (session == null)
            return;

        event.setCancelled(true);

        session.getGui().handleClick(player, event);

        session.markDirty();

        render(player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        GuiSession session = sessions.get(player.getUniqueId());

        if (session == null) {
            return;
        }

        event.setCancelled(true);

        session.getGui().handleDrag(player, event);

        render(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        GuiSession session = sessions.remove(player.getUniqueId());

        if (session == null) {
            return;
        }


        if (session.isClosing()) {
            return;
        }

        session.setClosing(true);

        session.getGui().onClose(player);
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {

        close(event.getPlayer());
    }

}
