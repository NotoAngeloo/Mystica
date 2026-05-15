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

        /*
         * Create fresh render context
         */

        GuiRenderContext context =
                new GuiRenderContext();

        /*
         * Allow GUI to submit
         * draw commands
         */

        gui.build(
                player,
                context
        );

        /*
         * Assemble final title
         */

        return assembler.assemble(
                context
        );
    }

}
