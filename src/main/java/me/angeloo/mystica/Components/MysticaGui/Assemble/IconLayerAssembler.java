package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.GlyphVariant;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderer;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderers;

import java.util.List;

public class IconLayerAssembler {

    private final GradientRenderers gradientRenderers;

    public IconLayerAssembler(Mystica main){
        gradientRenderers = main.getGradientRenderers();
    }

    public void assemble(StringBuilder builder, RenderCursor cursor, List<DrawCommand> commands){


        for(DrawCommand command : commands){

            if(command instanceof DrawGradientCommand gradient){

                GradientRenderer renderer = gradientRenderers.get(gradient.getDirection());

                cursor.seek(builder, gradient.getX());

                String result = renderer.render(gradient, gradient.getY());

                builder.append(result);

                cursor.advance(gradient.getWidth() + 1);

                continue;
            }

            if(!(command instanceof DrawIconCommand icon)){
                continue;
            }

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
        }

    }

}
