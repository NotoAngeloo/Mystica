package me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientDirection;

import java.awt.*;

public record DrawGradientCommand(
        int x,
        int y,
        int width,
        int height,
        Color start,
        Color end,
        GradientDirection direction)
        implements DrawCommand {

}
