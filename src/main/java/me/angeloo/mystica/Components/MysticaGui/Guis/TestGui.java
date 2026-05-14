package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.Command.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Command.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;
import org.bukkit.entity.Player;

public class TestGui extends Gui {

    @Override
    public void build(Player player, GuiRenderContext context){

        context.draw(RenderLayer.Text,
                new DrawTextCommand(0, 0, "test"));

        context.draw(RenderLayer.Content, new DrawIconCommand(0, 0, "\ue4e5"));

        setButton(0, (p, gui, event) -> {
            p.sendMessage("clicked");
        });

    }

}
