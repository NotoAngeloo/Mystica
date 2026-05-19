package me.angeloo.mystica.Components.MysticaGui.DrawCommand.BakgroundDrawCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

public class BackgroundDrawCommand implements DrawCommand {

    private final int x;

    private final Glyph glyph;

    public BackgroundDrawCommand(int x, Glyph glyph){
        this.x = x;
        this.glyph = glyph;
    }

    public int x() {
        return x;
    }

    public Glyph glyph() {
        return glyph;
    }

}
