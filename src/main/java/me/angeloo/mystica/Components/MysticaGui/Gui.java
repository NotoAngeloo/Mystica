package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class Gui {

    protected final Map<Integer, GuiButton> buttons =
            new HashMap<>();

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
     * Rendering
     * -----------------------------------------
     */

    public abstract void build(
            Player player,
            GuiRenderContext context
    );

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

    /*
     * -----------------------------------------
     * Buttons
     * -----------------------------------------
     */

    public void setButton(
            int slot,
            GuiButton button
    ) {

        buttons.put(slot, button);
    }

    public void removeButton(int slot) {

        buttons.remove(slot);
    }

    public void clearButtons() {

        buttons.clear();
    }


}
