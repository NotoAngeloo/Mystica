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

        for (int y = 0; y < command.getHeight(); y++) {

            int ascent = baseAscent - y;

            Color color = GradientUtil.sample(
                    GradientDirection.VERTICAL,
                    command.getStart(),
                    command.getEnd(),
                    0,
                    y,
                    command.getWidth(),
                    command.getHeight()
            );

            sb.append(ChatColor.of(color));

            char pixel = registry.get(ascent).getUnicode();

            for (int x = 0; x < command.getWidth(); x++) {

                sb.append(pixel);

                if (x < command.getWidth() - 1) {
                    sb.append("\uF801");
                }
            }

            if(y!=command.getHeight()-1){
                sb.append(UiSpacing.offset(-command.getWidth() - 1));
            }


        }

        sb.append(ChatColor.WHITE);

        return sb.toString();
    }

}
