package me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

public record DrawIconCommand(int row, int x, Glyph glyph) implements DrawCommand {

}
