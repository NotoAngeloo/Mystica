package me.angeloo.mystica.Utility.ShapeRenderer.Gradient;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;

public interface GradientRenderer {

    String render(
            DrawGradientCommand command,
            int baseAscent
    );

}
