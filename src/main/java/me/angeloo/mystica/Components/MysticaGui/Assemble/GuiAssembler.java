package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.GlyphVariant;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;

import java.util.Arrays;
import java.util.List;

public class GuiAssembler {

    private final ButtonLayerAssembler
            buttonAssembler =
            new ButtonLayerAssembler();

    public GuiRenderResult assemble(
            GuiRenderContext context
    ) {

        StringBuilder builder =
                new StringBuilder();

        for(RenderLayer layer
                : RenderLayer.values()) {

            List<DrawCommand> commands =
                    context.getLayer(layer);

            switch(layer) {

                case Buttons ->
                        buttonAssembler.assemble(
                                builder,
                                commands
                        );

                case Text ->
                        assembleText(
                                builder,
                                commands
                        );
            }
        }

        return new GuiRenderResult(
                builder.toString()
        );
    }

    private void assembleText(
            StringBuilder builder,
            List<DrawCommand> commands
    ) {

        for(DrawCommand command
                : commands) {

            if(command
                    instanceof DrawTextCommand text) {

                builder.append(
                        text.text()
                );
            }
        }
    }
}