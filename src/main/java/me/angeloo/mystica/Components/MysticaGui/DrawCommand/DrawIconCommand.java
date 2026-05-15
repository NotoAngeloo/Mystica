package me.angeloo.mystica.Components.MysticaGui.DrawCommand;

import me.angeloo.mystica.Components.MysticaGui.Font.GlyphVariant;

public record DrawIconCommand(int x, int y, GlyphVariant glyph) implements DrawCommand {

}
