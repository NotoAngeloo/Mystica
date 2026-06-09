package me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect;


import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Pages.*;
import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPage;


public class ClassSelect extends Gui {

    private final AssassinPage assassinPage;
    private final ElementalistPage elementalistPage;
    private final MysticPage mysticPage;
    private final PaladinPage paladinPage;
    private final RangerPage rangerPage;
    private final ShadowKnightPage shadowKnightPage;
    private final WarriorPage warriorPage;

    private SubClass selectedSubclass;

    public ClassSelect(GuiManager manager){
        this.assassinPage = new AssassinPage(this, manager);
        this.elementalistPage = new ElementalistPage(this, manager);
        this.mysticPage = new MysticPage(this, manager);
        this.paladinPage = new PaladinPage(this, manager);
        this.rangerPage = new RangerPage(this, manager);
        this.shadowKnightPage = new ShadowKnightPage(this, manager);
        this.warriorPage = new WarriorPage(this, manager);
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

    public MysticPage getMysticPage() {
        return mysticPage;
    }

    public PaladinPage getPaladinPage() {
        return paladinPage;
    }

    public RangerPage getRangerPage() {
        return rangerPage;
    }

    public ShadowKnightPage getShadowKnightPage() {
        return shadowKnightPage;
    }

    public WarriorPage getWarriorPage() {
        return warriorPage;
    }



    public SubClass getSelectedSubclass(){
        return selectedSubclass;
    }

    public void setSelectedSubclass(SubClass selectedSubclass){
        this.selectedSubclass = selectedSubclass;
    }
}
