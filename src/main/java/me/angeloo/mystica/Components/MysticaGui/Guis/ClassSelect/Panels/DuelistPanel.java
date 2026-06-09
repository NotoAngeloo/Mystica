package me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Panels;

import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPanel;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;

public class DuelistPanel extends GuiPanel {

    private final ClassSelect gui;

    public DuelistPanel(ClassSelect gui){
        this.gui = gui;
    }

    @Override
    public boolean isVisible(Player player){

        if(gui.getSelectedSubclass() == null){
            return false;
        }

        return gui.getSelectedSubclass().equals(SubClass.DUELIST);
    }

    @Override
    public void build(Player player, GuiRenderContext context){

        drawContent(context);

    }

    private void drawContent(GuiRenderContext context){



        //Add subclass name as well as roles
        context.drawTextContainer(100, 0, SubClass.DUELIST.getDescription());
    }

}
