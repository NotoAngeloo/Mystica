package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DrawTextContainerCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.*;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LayoutEngine;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LineData;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringGlyph;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringRenderer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TextContainerAssembler {


    private final LayoutEngine layoutEngine;
    private final StringRenderer stringRenderer;

    public TextContainerAssembler(Mystica main) {
        layoutEngine = main.getLayoutEngine();
        this.stringRenderer = main.getStringRenderer();
    }

    public void assemble(
            StringBuilder builder,
            RenderCursor cursor,
            List<DrawCommand> commands
    ) {

        for (DrawCommand command : commands) {

            if (!(command instanceof DrawTextContainerCommand container)) {
                continue;
            }


            List<LineData> lines = new ArrayList<>();

            for(String s : container.lines()){
                lines.add(new LineData(s, 8));
            }


            TextContainerLayout.ContainerLayout layout =
                    TextContainerLayout.measure(
                            lines,
                            container.y()
                    );

            cursor.seek(builder, container.x());

            drawNineSlice(builder, layout.container());


            //each col 16 pix
            cursor.advance((layout.container().columns()*16)+1);
            //cursor.advance(size.columns());


            List<StringGlyph> glyphs = layoutEngine.layout(lines);

            cursor.seek(builder, container.x() + TextContainerLayout.paddingX() + 1);


            builder.append(
                    stringRenderer.render(
                            glyphs,
                            container.y() + TextContainerLayout.paddingY()
                    )
            );

            int lastLineWidth = MinecraftCharWidths.getPixelWidth(container.lines().getLast());

            cursor.advance(lastLineWidth+1);

        }
    }



    private void drawNineSlice(
            StringBuilder builder,
            TextContainerLayout.ContainerSize size
    ) {
        int w = size.columns();
        int h = size.rows();

        for (int dy = 0; dy < h; dy++) {

            for (int dx = 0; dx < w; dx++) {

                Glyph region = resolveRegion(dx, dy, w, h);

                int variant = size.startVariant() + dy;

                builder.append(region.getVariant(variant).unicode());
                builder.append(UiSpacing.offset(-1));
            }

            if(dy!=h-1){
                builder.append(UiSpacing.offset(-16 * w));
            }


        }
    }

    private Glyph resolveRegion(int x, int y, int w, int h) {

        boolean left = x == 0;
        boolean right = x == w - 1;
        boolean top = y == 0;
        boolean bottom = y == h - 1;

        // corners
        if (top && left) return NineSlice.topLeft;
        if (top && right) return NineSlice.topRight;
        if (bottom && left) return NineSlice.bottomLeft;
        if (bottom && right) return NineSlice.bottomRight;

        // vertical edges only (no real "center" requirement anymore)
        if (top) return NineSlice.top;
        if (bottom) return NineSlice.bottom;

        // horizontal edges
        if (left) return NineSlice.left;
        if (right) return NineSlice.right;

        // optional fallback (not structurally required)
        return NineSlice.center;
    }




}
