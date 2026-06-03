package me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;

import java.util.List;

public class DrawTextContainerCommand implements DrawCommand {

    private final int x;
    private final int y;

    private final List<String> lines;

    public DrawTextContainerCommand(
            int x,
            int y,
            List<String> lines
    ) {

        this.x = x;
        this.y = y;

        this.lines = List.copyOf(lines);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public List<String> lines() {
        return lines;
    }

}
