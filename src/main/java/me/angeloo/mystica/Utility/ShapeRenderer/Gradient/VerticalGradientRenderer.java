package me.angeloo.mystica.Utility.ShapeRenderer.Gradient;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.UiSpacing;
import me.angeloo.mystica.Utility.ShapeRenderer.PixelGlyphRegistry;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public class VerticalGradientRenderer extends AbstractGradientRenderer{

    public VerticalGradientRenderer(PixelGlyphRegistry registry){
        super(registry);
    }

    //color evaluated once per row
    @Override
    public String render(DrawGradientCommand command, int baseAscent){
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < command.height(); y++) {

            int ascent = baseAscent - y;

            Color color = GradientUtil.sample(
                    GradientDirection.VERTICAL,
                    command.start(),
                    command.end(),
                    0,
                    y,
                    command.width(),
                    command.height()
            );

            sb.append(ChatColor.of(color));

            char pixel = registry.get(ascent).getUnicode();

            for (int x = 0; x < command.width(); x++) {

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
