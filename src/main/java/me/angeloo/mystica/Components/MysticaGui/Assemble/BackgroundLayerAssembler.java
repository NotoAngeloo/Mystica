package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.BackgroundDrawCommand.BackgroundDrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;

import java.util.List;

public class BackgroundLayerAssembler {

    public void assemble(StringBuilder builder, RenderCursor cursor, List<DrawCommand> commands){

        for (DrawCommand command : commands){

            if(!(command instanceof BackgroundDrawCommand bg)){
                continue;
            }

            Glyph glyph = bg.glyph();

            cursor.seek(builder, bg.x());

            builder.append(glyph.getVariant(0).unicode());

            //because space
            cursor.advance(glyph.width()+1);


        }

    }

}
