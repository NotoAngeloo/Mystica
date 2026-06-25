package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.ConstructedIcon;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawConstructedIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.GlyphVariant;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderer;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderers;
import me.angeloo.mystica.Utility.ShapeRenderer.Icon.ConstructedIconRenderer;

import java.util.List;

public class IconLayerAssembler {

    private final GradientRenderers gradientRenderers;
    private final ConstructedIconRenderer iconRenderer;

    public IconLayerAssembler(Mystica main){
        gradientRenderers = main.getGradientRenderers();
        iconRenderer = main.getConstructedIconRenderer();
    }

    public void assemble(StringBuilder builder, RenderCursor cursor, List<DrawCommand> commands){


        for(DrawCommand command : commands){

            if(command instanceof DrawGradientCommand gradient){

                GradientRenderer renderer = gradientRenderers.get(gradient.direction());

                cursor.seek(builder, gradient.x());

                String result = renderer.render(gradient, gradient.y());

                builder.append(result);

                cursor.advance(gradient.width() + 1);

                continue;
            }

            if(command instanceof DrawIconCommand icon){
                Glyph glyph = icon.glyph();

                GlyphVariant variant = glyph.getVariant(icon.row());

                if(variant==null){
                    continue;
                }

                cursor.seek(builder, icon.x());

                builder.append(variant.unicode());

                cursor.advance(glyph.width()+1);


                //reset after each???
                //cursor.seek(builder, 0);
                continue;
            }

            if(command instanceof DrawConstructedIconCommand constructed){

                ConstructedIcon icon = constructed.icon();

                cursor.seek(builder,constructed.x());

                String result = iconRenderer.render(constructed, constructed.y());

                builder.append(result);

                cursor.advance(icon.width() + 1);

                continue;
            }

        }

    }

}
