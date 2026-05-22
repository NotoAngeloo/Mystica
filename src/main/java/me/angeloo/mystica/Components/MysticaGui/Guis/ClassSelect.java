package me.angeloo.mystica.Components.MysticaGui.Guis;


import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.Pages.AssassinPage;
import me.angeloo.mystica.Components.MysticaGui.Pages.ElementalistPage;
import me.angeloo.mystica.Components.MysticaGui.Pages.GuiPage;


public class ClassSelect extends Gui {

    private final AssassinPage assassinPage;
    private final ElementalistPage elementalistPage;

    public ClassSelect(GuiManager manager){
        this.assassinPage = new AssassinPage(this, manager);
        this.elementalistPage = new ElementalistPage(this, manager);
    }

    @Override
    public GuiPage getInitialPage() {
        return assassinPage;
    }

    public AssassinPage getAssassinPage() {
        return assassinPage;
    }

    public ElementalistPage getElementalistPage() {
        return elementalistPage;
    }
}
