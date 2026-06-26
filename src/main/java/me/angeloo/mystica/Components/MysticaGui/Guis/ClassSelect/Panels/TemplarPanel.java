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

public class TemplarPanel extends GuiPanel {

    private final ClassSelect gui;

    public TemplarPanel(ClassSelect gui){
        this.gui = gui;
    }

    @Override
    public boolean isVisible(Player player){

        if(gui.getSelectedSubclass() == null){
            return false;
        }

        return gui.getSelectedSubclass().equals(SubClass.TEMPLAR);
    }

    @Override
    public void build(Player player, GuiRenderContext context){

        drawContent(context);

    }

    private void drawContent(GuiRenderContext context){

        context.drawDescriptionCard(
                180,
                -32,

                ConstructedIcons.TEMPLAR_ICON,

                List.of(
                        SubClass.TEMPLAR.getDisplayName(),
                        SubClass.TEMPLAR.getRange().getDisplayName()
                                + " " +SubClass.TEMPLAR.getRole().getDisplayName(),
                        SubClass.TEMPLAR.getDamageType().getDisplayName()
                ),

                SubClass.TEMPLAR.getDescription(),

                new CardStyle(
                        PlayerClass.PALADIN.getColor(),
                        Color.BLACK,
                        GradientDirection.HORIZONTAL

                ));

    }
}
