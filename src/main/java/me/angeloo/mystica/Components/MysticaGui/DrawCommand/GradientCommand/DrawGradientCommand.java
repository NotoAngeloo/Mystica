package me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientDirection;

import java.awt.*;

public class DrawGradientCommand implements DrawCommand {

    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final Color start;
    private final Color end;

    private final GradientDirection direction;

    public DrawGradientCommand(int x, int y, int width, int height, Color start, Color end, GradientDirection direction) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.start = start;
        this.end = end;
        this.direction = direction;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getStart() {
        return start;
    }

    public Color getEnd() {
        return end;
    }

    public GradientDirection getDirection() {
        return direction;
    }
}
