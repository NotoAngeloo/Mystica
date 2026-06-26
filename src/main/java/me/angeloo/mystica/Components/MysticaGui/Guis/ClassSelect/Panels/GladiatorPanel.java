package me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Panels;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.CardStyle;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPanel;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientDirection;
import me.angeloo.mystica.Utility.ShapeRenderer.Icon.ConstructedIcons;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;

public class GladiatorPanel extends GuiPanel {

    private final ClassSelect gui;

    public GladiatorPanel(ClassSelect gui){
        this.gui = gui;
    }

    @Override
    public boolean isVisible(Player player){

        if(gui.getSelectedSubclass() == null){
            return false;
        }

        return gui.getSelectedSubclass().equals(SubClass.GLADIATOR);
    }

    @Override
    public void build(Player player, GuiRenderContext context){

        drawContent(context);

    }

    private void drawContent(GuiRenderContext context){

        context.drawDescriptionCard(
                180,
                -32,

                ConstructedIcons.GLADIATOR_ICON,

                List.of(
                        SubClass.GLADIATOR.getDisplayName(),
                        SubClass.GLADIATOR.getRange().getDisplayName()
                                + " " +SubClass.GLADIATOR.getRole().getDisplayName(),
                        SubClass.GLADIATOR.getDamageType().getDisplayName()
                ),

                SubClass.GLADIATOR.getDescription(),

                new CardStyle(
                        PlayerClass.WARRIOR.getColor(),
                        Color.BLACK,
                        GradientDirection.HORIZONTAL

                ));

    }

}
