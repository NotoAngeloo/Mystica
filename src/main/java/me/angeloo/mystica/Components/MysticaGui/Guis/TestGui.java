package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;
import org.bukkit.entity.Player;

public class TestGui extends Gui {

    @Override
    public void build(
            Player player,
            GuiRenderContext context
    ) {



        context.drawButton(
                81,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                89,
                UiGlyphs.RIGHT_ARROW
        );

    }

}
