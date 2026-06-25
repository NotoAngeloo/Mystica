package me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.ConstructedIcon;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

import java.util.List;

public record DescriptionCardCommand(int x, int y, ConstructedIcon icon, List<String> title, List<String> description,
                                     CardStyle style) implements DrawCommand {

}
