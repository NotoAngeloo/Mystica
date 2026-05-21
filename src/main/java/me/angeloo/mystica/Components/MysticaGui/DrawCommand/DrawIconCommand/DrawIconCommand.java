package me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

public class DrawIconCommand implements DrawCommand {

    private final int row;

    private final int x;

    private final Glyph glyph;

    public DrawIconCommand(int row, int x, Glyph glyph){
        this.row = row;
        this.x = x;
        this.glyph = glyph;
    }

    public int row() {
        return row;
    }

    public int x() {
        return x;
    }

    public Glyph glyph() {
        return glyph;
    }

}
