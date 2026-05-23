package me.angeloo.mystica.Components.MysticaGui;


import me.angeloo.mystica.Components.MysticaGui.Guis.Pages.GuiPage;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import org.bukkit.inventory.Inventory;

public class GuiSession {

    private final Gui gui;

    private Inventory inventory;

    private GuiPage currentPage;

    private GuiRenderResult lastRender;

    private boolean dirty = true;

    private boolean closing = false;

    public GuiSession(
            Gui gui,
            GuiPage currentPage
    ) {

        this.gui = gui;
        this.currentPage = currentPage;
    }

    public Gui getGui() {
        return gui;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(
            Inventory inventory
    ) {

        this.inventory = inventory;
    }

    public GuiPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(
            GuiPage currentPage
    ) {

        this.currentPage = currentPage;

        markDirty();
    }

    public GuiRenderResult getLastRender() {
        return lastRender;
    }

    public void setLastRender(
            GuiRenderResult lastRender
    ) {

        this.lastRender = lastRender;
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
        dirty = true;
    }

    public void clearDirty() {
        dirty = false;
    }

}
