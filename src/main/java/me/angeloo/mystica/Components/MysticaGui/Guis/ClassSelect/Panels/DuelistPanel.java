package me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Panels;

import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.CardStyle;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPanel;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientDirection;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderers;
import org.bukkit.entity.Player;

import java.awt.*;

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

        context.drawDescriptionCard(
                100,
                -32,
                SubClass.DUELIST.getDisplayName(),
                SubClass.DUELIST.getDescription(),
                new CardStyle(
                        Color.RED,
                        Color.BLACK,
                        GradientDirection.HORIZONTAL

                ));

        /*context.drawGradient(
                100,
                -100,
                50,
                50,
                Color.GREEN,
                Color.RED,
                GradientDirection.TOP_CORNER);

        //Add subclass name as well as roles
        context.drawTextContainer(100, 0, SubClass.DUELIST.getDescription());*/
    }

}
