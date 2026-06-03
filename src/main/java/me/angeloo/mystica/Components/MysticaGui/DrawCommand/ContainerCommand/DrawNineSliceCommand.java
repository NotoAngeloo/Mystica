package me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;

import java.util.List;

public class DrawNineSliceCommand implements DrawCommand {

    private final int x;
    private final int y;

    private final int columns;
    private final int rows;

    private final int variant;

    public DrawNineSliceCommand(
            int x,
            int y,
            int columns,
            int rows,
            int variant
    ) {

        this.x = x;
        this.y = y;

        this.columns = columns;
        this.rows = rows;

        this.variant = variant;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

    public int variant() {
        return variant;
    }


}
