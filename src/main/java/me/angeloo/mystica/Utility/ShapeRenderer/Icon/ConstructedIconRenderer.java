package me.angeloo.mystica.Utility.ShapeRenderer.Icon;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawConstructedIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.UiSpacing;
import me.angeloo.mystica.Utility.ShapeRenderer.PixelGlyphRegistry;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConstructedIconRenderer {

    private final PixelGlyphRegistry registry;

    public ConstructedIconRenderer(PixelGlyphRegistry registry){
        this.registry = registry;
    }

    public String render(
            DrawConstructedIconCommand command,
            int baseAscent
    ) {

        StringBuilder sb = new StringBuilder();

        BufferedImage image = command.icon().icon();

        Color previous = null;

        for (int y = 0; y < image.getHeight(); y++) {

            char pixel =
                    registry.get(baseAscent - y)
                            .getUnicode();

            for (int x = 0; x < image.getWidth(); x++) {

                Color color = new Color(
                        image.getRGB(x, y)
                );

                if (!color.equals(previous)) {
                    sb.append(ChatColor.of(color));
                    previous = color;
                }

                sb.append(pixel);

                if (x < image.getWidth() - 1) {
                    sb.append("\uF801");
                }
            }

            if (y != image.getHeight() - 1) {
                sb.append(
                        UiSpacing.offset(
                                -image.getWidth() - 1
                        )
                );
            }
        }

        sb.append(ChatColor.WHITE);

        return sb.toString();
    }

}
