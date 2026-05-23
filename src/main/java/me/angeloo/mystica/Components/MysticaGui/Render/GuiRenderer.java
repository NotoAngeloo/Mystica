package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.Assemble.GuiAssembler;

import me.angeloo.mystica.Components.MysticaGui.GuiSession;

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
            GuiSession session
    ) {

        GuiRenderContext context =
                new GuiRenderContext(
                        player,
                        session
                );

        session.getCurrentPage().render(
                player,
                context
        );

        return assembler.assemble(
                context
        );
    }

}
