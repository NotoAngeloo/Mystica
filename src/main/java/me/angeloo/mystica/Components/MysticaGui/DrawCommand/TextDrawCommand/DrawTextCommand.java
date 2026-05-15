package me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;

public class DrawTextCommand implements DrawCommand {

    private final String text;

    public DrawTextCommand(
            String text
    ) {

        this.text = text;
    }

    public String text() {

        return text;
    }

}
