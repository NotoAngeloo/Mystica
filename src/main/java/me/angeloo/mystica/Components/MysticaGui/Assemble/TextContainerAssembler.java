package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DrawTextContainerCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.*;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.TextRenderer.LayoutEngine;
import me.angeloo.mystica.Utility.TextRenderer.LineData;
import me.angeloo.mystica.Utility.TextRenderer.StringGlyph;
import me.angeloo.mystica.Utility.TextRenderer.StringRenderer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TextContainerAssembler {

    private static final int PADDING_X = 2;
    private static final int PADDING_Y = 1;

    // logical line step in glyph space

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

            /*
             * 1. Layout
             */

            List<LineData> lines = new ArrayList<>();

            for(String s : container.lines()){
                lines.add(new LineData(s, 8));
            }

            TextDimensions dim = measure(lines); //in pixels
            ContainerSize size = calculateSize(dim);

            /*
             * 2. Render 9-slice background
             */

            drawNineSlice(builder, container.x(), container.y(), size);

            /*
             * 3. Render text inside (NO cursor advancement per line)
             */

            List<StringGlyph> glyphs = layoutEngine.layout(lines);

            cursor.seek(builder, container.x() + PADDING_X);

            builder.append(
                    stringRenderer.render(
                            glyphs,
                            container.y() + PADDING_Y
                    )
            );

            /*
             * 4. SINGLE cursor advance for entire container
             */

            cursor.advance(size.columns());
        }
    }


    /*
     * ----------------------------------------
     * Measurement
     * ----------------------------------------
     */

    private TextDimensions measure(List<LineData> lines) {

        int maxWidth = 0;
        int totalHeight = lines.size() * 8;


        for (LineData line : lines) {
            int width = MinecraftCharWidths.getPixelWidth(line.text());
            maxWidth = Math.max(maxWidth, width);
        }

        return new TextDimensions(maxWidth, totalHeight);
    }

    //TODO: the text and the grid are on two different measurment scales. the grid needs to be more generous

    private ContainerSize calculateSize(TextDimensions dim) {

        int width = dim.width() + (PADDING_X * 2);
        int height = dim.height() + (PADDING_Y * 2);


        int cols = Math.max(2,(int) Math.ceil((double)width/16));
        int rows = Math.max(2,(int) Math.ceil((double)height/16));

        return new ContainerSize(cols, rows);
    }


    private record ContainerSize(int columns, int rows) {}

    private void drawNineSlice(
            StringBuilder builder,
            int x,
            int y,
            ContainerSize size
    ) {
        int w = size.columns();
        int h = size.rows();

        for (int dy = 0; dy < h; dy++) {

            for (int dx = 0; dx < w; dx++) {

                Glyph region = resolveRegion(dx, dy, w, h);

                int variant = dy;

                builder.append(region.getVariant(variant).unicode());
                builder.append(UiSpacing.offset(-1));
            }

            builder.append(UiSpacing.offset(-16 * w));
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

    private int rowBand(int y, int h) {

        if (h <= 1) return 0;

        if (h <= 14) {
            return y; // direct mapping, perfect case
        }

        // compress into 0–13 range
        return (int) ((y / (double)(h - 1)) * 13);
    }



}
