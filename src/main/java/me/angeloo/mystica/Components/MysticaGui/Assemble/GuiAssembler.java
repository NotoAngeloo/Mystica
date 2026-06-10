package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderResult;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;
import me.angeloo.mystica.Mystica;

import java.util.List;

public class GuiAssembler {


    private final BackgroundLayerAssembler backgroundAssembler;
    private final IconLayerAssembler iconLayerAssembler;
    private final TextContainerAssembler textContainerAssembler;
    private final ButtonLayerAssembler buttonAssembler ;
    private final TextLayerAssembler textLayerAssembler;

    public GuiAssembler(Mystica main){
        backgroundAssembler = new BackgroundLayerAssembler();
        iconLayerAssembler  = new IconLayerAssembler(main);
        textContainerAssembler = new TextContainerAssembler(main);
        buttonAssembler = new ButtonLayerAssembler();
        textLayerAssembler = new TextLayerAssembler(main);
    }

    public GuiRenderResult assemble(
            GuiRenderContext context
    ) {

        StringBuilder builder =
                new StringBuilder();

        RenderCursor cursor = new RenderCursor();

        for(RenderLayer layer
                : RenderLayer.values()) {

            List<DrawCommand> commands = context.getLayer(layer);

            switch(layer) {

                case Background -> backgroundAssembler.assemble(
                        builder, cursor, commands
                );

                case Icons -> iconLayerAssembler.assemble(builder, cursor, commands);

                case Container -> textContainerAssembler.assemble(builder, cursor, commands);

                case Buttons ->
                        buttonAssembler.assemble(
                                builder,
                                cursor,
                                commands
                        );

                case Text -> textLayerAssembler.assemble(builder, cursor, commands);
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

}