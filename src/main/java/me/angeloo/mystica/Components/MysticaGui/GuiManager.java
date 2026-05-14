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

    private final Map<UUID, Gui> activeGuis =
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

        Gui current =
                activeGuis.get(player.getUniqueId());

        if(current != null) {

            current.onClose(player);
        }

        activeGuis.put(
                player.getUniqueId(),
                gui
        );

        gui.onOpen(player);

        render(player);
    }

    public void close(Player player) {

        Gui gui =
                activeGuis.remove(
                        player.getUniqueId()
                );

        if(gui == null)
            return;

        gui.onClose(player);

        player.closeInventory();
    }

    /*
     * -----------------------------------------
     * Access
     * -----------------------------------------
     */

    public Gui get(Player player) {

        return activeGuis.get(
                player.getUniqueId()
        );
    }

    public boolean hasGui(Player player) {

        return activeGuis.containsKey(
                player.getUniqueId()
        );
    }

    /*
     * -----------------------------------------
     * Rendering
     * -----------------------------------------
     */

    public void render(Player player) {

        Gui gui = get(player);

        if(gui == null)
            return;

        GuiRenderResult result =
                renderer.render(
                        player,
                        gui
                );

        openInventory(
                player,
                result
        );
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
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        if(!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        Gui gui = get(player);

        if(gui == null)
            return;

        event.setCancelled(true);

        gui.handleClick(
                player,
                event
        );

        /*
         * Optional immediate rerender.
         */

        //render(player);
    }

    @EventHandler
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {

        if(!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        Gui gui = get(player);

        if(gui == null)
            return;

        event.setCancelled(true);

        gui.handleDrag(
                player,
                event
        );

        //render(player);
    }

    @EventHandler
    public void onInventoryClose(
            InventoryCloseEvent event
    ) {

        if(!(event.getPlayer()
                instanceof Player player)) {

            return;
        }

        Gui gui = get(player);

        if(gui == null)
            return;

        gui.onClose(player);

        activeGuis.remove(
                player.getUniqueId()
        );
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {

        close(event.getPlayer());
    }

}
