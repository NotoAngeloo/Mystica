package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;

public abstract class GuiPanel {

    public abstract void build(Player player, GuiRenderContext context);

    public abstract boolean isVisible(Player player);
}
