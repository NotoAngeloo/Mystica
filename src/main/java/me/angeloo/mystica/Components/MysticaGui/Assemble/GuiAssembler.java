package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.Command.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Command.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Command.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;

import java.util.List;

public class GuiAssembler {

    public GuiRenderResult assemble(
            GuiRenderContext context
    ) {

        StringBuilder builder =
                new StringBuilder();

        for(RenderLayer layer
                : RenderLayer.values()) {

            List<DrawCommand> commands =
                    context.getLayer(layer);

            for(DrawCommand command
                    : commands) {

                if(command
                        instanceof DrawTextCommand text) {

                    builder.append(
                            text.text()
                    );
                }

                else if(command
                        instanceof DrawIconCommand icon) {

                    builder.append(
                            icon.glyph()
                    );
                }
            }
        }

        return new GuiRenderResult(
                builder.toString()
        );
    }

}
