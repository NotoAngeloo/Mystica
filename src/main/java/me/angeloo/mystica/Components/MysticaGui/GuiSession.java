package me.angeloo.mystica.Components.MysticaGui;

import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import org.bukkit.inventory.Inventory;

public class GuiSession {

    private final Gui gui;

    private final Inventory inventory;

    private GuiRenderResult lastRender;

    private boolean dirty = true;

    private boolean closing = false;

    public GuiSession(Gui gui, Inventory inventory){
        this.gui = gui;
        this.inventory = inventory;
    }

    public Gui getGui(){
        return gui;
    }

    public Inventory getInventory(){
        return inventory;
    }

    public GuiRenderResult getLastRender(){
        return lastRender;
    }

    public void setLastRender(GuiRenderResult guiRenderResult){
        this.lastRender = guiRenderResult;
    }

    public boolean isClosing() {
        return closing;
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void clearDirty() {
        this.dirty = false;
    }

}
