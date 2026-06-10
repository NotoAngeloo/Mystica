package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.TextDrawCommand.DrawTextCommand;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LayoutEngine;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringGlyph;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringRenderer;

import java.util.List;

public class TextLayerAssembler {

    private final LayoutEngine layoutEngine;
    private final StringRenderer stringRenderer;

    public TextLayerAssembler(Mystica main){
        layoutEngine = main.getLayoutEngine();
        stringRenderer = main.getStringRenderer();

    }

    public void assemble(StringBuilder builder, RenderCursor cursor, List<DrawCommand> commands){


        for(DrawCommand command : commands){


            if(!(command instanceof DrawTextCommand text)){
                continue;
            }

            cursor.seek(builder, text.x());

            List<StringGlyph> glyphs = layoutEngine.layout(text.data());

            //builder.append(text.color());

            builder.append(stringRenderer.render(glyphs, text.y()));

            cursor.advance(glyphs.getLast().getWidth()+1);

        }

    }



}
