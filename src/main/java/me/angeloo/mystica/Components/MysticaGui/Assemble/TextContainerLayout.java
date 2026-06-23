package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.Font.MinecraftCharWidths;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LineData;

import java.util.List;

public final class TextContainerLayout {

    private static final int PADDING_X = 2;
    private static final int PADDING_Y = 1;

    private TextContainerLayout() {}

    public static ContainerLayout measure(
            List<LineData> lines,
            int textY
    ) {

        TextDimensions dimensions = measureText(lines);
        ContainerSize container = calculateSize(dimensions, textY);

        return new ContainerLayout(
                dimensions,
                container
        );
    }

    private static TextDimensions measureText(
            List<LineData> lines
    ) {

        int maxWidth = 0;
        int totalHeight = lines.size() * 8;

        for (LineData line : lines) {
            int width =
                    MinecraftCharWidths.getPixelWidth(
                            line.text()
                    );

            maxWidth = Math.max(maxWidth, width);
        }

        return new TextDimensions(
                maxWidth,
                totalHeight
        );
    }

    private static ContainerSize calculateSize(
            TextDimensions dim,
            int textY
    ) {

        int width = dim.width() + (PADDING_X * 2);

        int cols = Math.max(
                2,
                (int) Math.ceil(width / 16.0)
        );

        int topVariant =
                Math.max(0, (-textY) / 16);

        int bottomY =
                textY - dim.height();

        int bottomVariant =
                Math.max(
                        0,
                        (int) Math.ceil((-bottomY) / 16.0) - 1
                );

        int rows =
                Math.max(
                        2,
                        (bottomVariant - topVariant) + 1
                );

        return new ContainerSize(
                cols,
                rows,
                topVariant
        );
    }

    public record ContainerLayout(
            TextDimensions text,
            ContainerSize container
    ) {}

    public record TextDimensions(
            int width,
            int height
    ) {}

    public record ContainerSize(
            int columns,
            int rows,
            int startVariant
    ) {}

    public static int paddingX() {
        return PADDING_X;
    }

    public static int paddingY() {
        return PADDING_Y;
    }
}
