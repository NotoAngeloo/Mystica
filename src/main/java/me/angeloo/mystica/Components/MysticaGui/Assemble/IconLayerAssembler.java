package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.GlyphVariant;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;

import java.util.List;

public class IconLayerAssembler {

    public void assemble(StringBuilder builder, RenderCursor cursor, List<DrawCommand> commands){


        for(DrawCommand command : commands){


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
