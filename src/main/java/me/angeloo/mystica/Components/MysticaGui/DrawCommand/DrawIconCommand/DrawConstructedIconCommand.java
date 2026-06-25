package me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;

import java.awt.image.BufferedImage;

public record DrawConstructedIconCommand(
        int x,
        int y,
        ConstructedIcon icon) implements DrawCommand {
}
