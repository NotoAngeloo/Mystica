package me.angeloo.mystica.Utility.ShapeRenderer.Gradient;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.UiSpacing;
import me.angeloo.mystica.Utility.ShapeRenderer.PixelGlyphRegistry;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public class HorizontalGradientRenderer extends AbstractGradientRenderer{

    public HorizontalGradientRenderer(PixelGlyphRegistry registry){
        super(registry);
    }

    @Override
    public String render(
            DrawGradientCommand command,
            int baseAscent
    ) {

        StringBuilder sb = new StringBuilder();

        Color previous = null;

        for (int y = 0; y < command.height(); y++) {

            char pixel =
                    registry.get(baseAscent - y)
                            .getUnicode();

            for (int x = 0; x < command.width(); x++) {

                Color color = GradientUtil.sample(
                        GradientDirection.HORIZONTAL,
                        command.start(),
                        command.end(),
                        x,
                        y,
                        command.width(),
                        command.height()
                );

                if (!color.equals(previous)) {
                    sb.append(ChatColor.of(color));
                    previous = color;
                }

                sb.append(pixel);

                if (x < command.width() - 1) {
                    sb.append("\uF801");
                }
            }

            if(y!=command.height()-1){
                sb.append(UiSpacing.offset(-command.width() - 1));
            }
        }

        sb.append(ChatColor.WHITE);

        return sb.toString();
    }
}
