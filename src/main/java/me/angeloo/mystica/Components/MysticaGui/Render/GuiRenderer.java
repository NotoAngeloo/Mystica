package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.Assemble.GuiAssembler;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import org.bukkit.entity.Player;

public class GuiRenderer {

    private final GuiAssembler assembler;

    public GuiRenderer(
            GuiAssembler assembler
    ) {

        this.assembler = assembler;
    }

    public GuiRenderResult render(
            Player player,
            Gui gui
    ) {

        GuiRenderContext context =
                new GuiRenderContext();

        /*
         * GUI describes itself.
         */

        gui.build(player, context);

        /*
         * Convert render commands into
         * final unicode/title output.
         */

        return assembler.assemble(context);
    }

}
