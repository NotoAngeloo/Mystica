package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;

import java.util.List;

public class GuiAssembler {

    private final ButtonLayerAssembler buttonAssembler = new ButtonLayerAssembler();

    private final BackgroundLayerAssembler backgroundAssembler = new BackgroundLayerAssembler();

    public GuiRenderResult assemble(
            GuiRenderContext context
    ) {

        StringBuilder builder =
                new StringBuilder();

        RenderCursor cursor = new RenderCursor();

        for(RenderLayer layer
                : RenderLayer.values()) {

            List<DrawCommand> commands =
                    context.getLayer(layer);

            switch(layer) {

                case Background -> backgroundAssembler.assemble(
                        builder, cursor, commands
                );

                case Buttons ->
                        buttonAssembler.assemble(
                                builder,
                                cursor,
                                commands
                        );

                /*case Text ->
                        assembleText(
                                builder,
                                cursor,
                                commands
                        );*/
            }

            /*
             * Restore baseline alignment
             * after each layer
             */

            cursor.seek(builder, 0);
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