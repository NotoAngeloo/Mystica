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

        /*
         * Row 0
         */

        context.drawButton(
                0,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                1,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                2,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                8,
                UiGlyphs.LEFT_ARROW
        );

        /*
         * Row 1
         */

        context.drawButton(
                9,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                10,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                16,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                17,
                UiGlyphs.LEFT_ARROW
        );

        /*
         * Row 2
         */

        context.drawButton(
                18,
                UiGlyphs.LEFT_ARROW
        );

        context.drawButton(
                27,
                UiGlyphs.LEFT_ARROW
        );
    }

}
