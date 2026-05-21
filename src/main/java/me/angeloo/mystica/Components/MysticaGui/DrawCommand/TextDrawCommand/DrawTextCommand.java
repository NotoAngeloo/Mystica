package me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Utility.TextRenderer.LineData;
import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class DrawTextCommand implements DrawCommand {

    private final int x;
    private final int y;
    private final List<LineData> data;

    public DrawTextCommand(int x, int y, List<LineData> data) {
        this.x = x;
        this.y = y;
        this.data = data;
    }

    public List<LineData> data() {
        return data;
    }

    public int x() {
        return x;
    }
    public int y(){
        return y;
    }

}
