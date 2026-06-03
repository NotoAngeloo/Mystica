package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.BackgroundDrawCommand.BackgroundDrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DrawNineSliceCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DrawTextContainerCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.GuiSession;
import me.angeloo.mystica.Utility.TextRenderer.LineData;
import org.bukkit.entity.Player;

import java.util.*;

public class GuiRenderContext {

    private final Player player;

    private final GuiSession session;

    private final Map<
            RenderLayer,
            List<DrawCommand>
            > layers =
            new EnumMap<>(RenderLayer.class);

    public GuiRenderContext(
            Player player,
            GuiSession session
    ) {

        this.player = player;
        this.session = session;
    }

    /*
     * ----------------------------------------
     * Context Access
     * ----------------------------------------
     */

    public Player getPlayer() {
        return player;
    }

    public GuiSession getSession() {
        return session;
    }

    /*
     * ----------------------------------------
     * Drawing
     * ----------------------------------------
     */

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
     * Helpers
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
    ) {

        draw(
                RenderLayer.Background,
                new BackgroundDrawCommand(
                        x,
                        glyph
                )
        );
    }

    public void drawIcon(
            int row,
            int x,
            Glyph glyph
    ) {

        draw(
                RenderLayer.Icons,
                new DrawIconCommand(
                        row,
                        x,
                        glyph
                )
        );
    }

    public void drawText(
            int x,
            int y,
            List<LineData> data
    ) {

        draw(
                RenderLayer.Text,
                new DrawTextCommand(
                        x,
                        y,
                        data
                )
        );
    }

    //perhaps in future make this able to make a container for non-text
    public void drawTextContainer(
            int x,
            int y,
            List<String> lines
    ){

        draw(
                RenderLayer.Container,
                new DrawTextContainerCommand(
                        x,
                        y,
                        lines
                )
        );

    }

    /*
     * ----------------------------------------
     * Layer Access
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
