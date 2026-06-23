package me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand;

import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientDirection;

import java.awt.*;

public record CardStyle(

        Color startColor,
        Color endColor,

        GradientDirection gradientDirection

        ){
}
