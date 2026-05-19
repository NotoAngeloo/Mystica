package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.BakgroundDrawCommand.BackgroundDrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

import java.util.*;

public class GuiRenderContext {

    private final Map<RenderLayer,
            List<DrawCommand>> layers =
            new EnumMap<>(RenderLayer.class);

    public void draw(
            RenderLayer layer,
            DrawCommand command
    ) {

        layers
                .computeIfAbsent(
                        layer,
                        k -> new ArrayList<>()
                )
                .add(command);
    }

    /*
     * ----------------------------------------
     * BUTTON HELPERS
     * ----------------------------------------
     */

    public void drawButton(
            int slot,
            Glyph glyph
    ) {

        draw(
                RenderLayer.Buttons,
                new DrawSlotIconCommand(
                        slot,
                        glyph
                )
        );
    }


    public void drawBackground(
            int x,
            Glyph glyph
    ){
        draw(RenderLayer.Background,
                new BackgroundDrawCommand(x, glyph));
    }

    /*
     * ----------------------------------------
     * LAYER ACCESS
     * ----------------------------------------
     */

    public List<DrawCommand> getLayer(
            RenderLayer layer
    ) {

        return layers.getOrDefault(
                layer,
                Collections.emptyList()
        );
    }

}
