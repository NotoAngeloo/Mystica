package me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;

import java.util.List;

public class DescriptionCardCommand implements DrawCommand {

    private final int x;
    private final int y;

    private final String title;
    private final List<String> description;

    private final CardStyle style;

    public DescriptionCardCommand(int x, int y, String title, List<String> description, CardStyle style) {
        this.x = x;
        this.y = y;
        this.title = title;
        this.description = description;
        this.style = style;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    public CardStyle getStyle() {
        return style;
    }
}
