package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.BackgroundDrawCommand.BackgroundDrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Utility.TextRenderer.LineData;

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
     * HELPERS
     * ----------------------------------------
     */

    public void drawButton(
            int slot,
            Glyph glyph
    ) {

        draw(
                RenderLayer.Buttons,
                new DrawSlotCommand(
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

    public void drawIcon(int row, int x, Glyph glyph){
        draw(RenderLayer.Icons, new DrawIconCommand(row, x, glyph));
    }

    public void drawText(int x, int y, List<LineData> data){
        draw(RenderLayer.Text, new DrawTextCommand(x, y, data));
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
