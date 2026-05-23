package me.angeloo.mystica.Components.MysticaGui.Guis.Pages;

import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiPage {

    protected final Map<Integer, GuiButton> buttons =
            new HashMap<>();

    /*
     * -----------------------------------------
     * Rendering
     * -----------------------------------------
     */

    public final void render(
            Player player,
            GuiRenderContext context
    ) {

        /*
         * Prevent stale interactions
         */

        clearButtons();

        /*
         * Build page
         */

        build(
                player,
                context
        );

    }

    protected abstract void build(
            Player player,
            GuiRenderContext context
    );

    /*
     * -----------------------------------------
     * Button Rendering
     * -----------------------------------------
     */

    protected void button(
            GuiRenderContext context,
            GuiButton button
    ) {

        /*
         * Register interactions
         */

        setButton(button);

        /*
         * Render origin slot only
         */

        context.drawButton(
                button.slot(),
                button.glyph()
        );
    }

    /*
     * -----------------------------------------
     * Input
     * -----------------------------------------
     */

    public void handleClick(
            Player player,
            InventoryClickEvent event
    ) {

        GuiButton button =
                buttons.get(event.getRawSlot());

        if(button == null)
            return;

        button.click(
                player,
                this,
                event
        );
    }

    public void handleDrag(
            Player player,
            InventoryDragEvent event
    ) {

    }

    public void handleClose(Player player, InventoryCloseEvent event){

    }

    /*
     * -----------------------------------------
     * Buttons
     * -----------------------------------------
     */

    public void setButton(
            GuiButton button
    ) {

        for(int slot
                : button.interactionSlots()) {

            buttons.put(
                    slot,
                    button
            );
        }
    }

    public void removeButton(int slot) {

        buttons.remove(slot);
    }

    public void clearButtons() {
        buttons.clear();
    }

}
