package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DescriptionCardCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DrawTextContainerCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.MinecraftCharWidths;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientDirection;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderer;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderers;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LayoutEngine;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LineData;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringGlyph;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringRenderer;

import java.util.ArrayList;
import java.util.List;

public class DescriptionCardAssembler {

    private final GradientRenderers gradientRenderers;
    private final LayoutEngine layoutEngine;
    private final StringRenderer stringRenderer;
    private final TextContainerAssembler textContainerAssembler;

    public DescriptionCardAssembler(Mystica main, TextContainerAssembler textContainerAssembler) {
        gradientRenderers = main.getGradientRenderers();
        layoutEngine = main.getLayoutEngine();
        stringRenderer = main.getStringRenderer();
        this.textContainerAssembler = textContainerAssembler;
    }

    public void assemble(
            StringBuilder builder,
            RenderCursor cursor,
            List<DrawCommand> commands
    ) {

        for (DrawCommand command : commands) {

            if (!(command instanceof DescriptionCardCommand card)) {
                continue;
            }

            /*
             * ----------------------------------------
             * 1. SAFE BODY ANCHOR (AVOID TOP VARIANT)
             * ----------------------------------------
             */

            int bodyY = (card.getY() / 16) * 16 - 16;
            int headerHeight = 16; //TODO: make header title multi-line and derive height from that
            int headerY = bodyY + headerHeight + 4;

            //header can NOT be above y = 0. if cY=0, bY = -16

            /*
             * ----------------------------------------
             * 2. BODY LAYOUT (SOURCE OF TRUTH)
             * ----------------------------------------
             */

            List<LineData> lines = new ArrayList<>();
            for (String s : card.getDescription()) {
                lines.add(new LineData(s, 8));
            }

            TextContainerLayout.ContainerLayout bodyLayout =
                    TextContainerLayout.measure(lines, bodyY);

            int bodyCols = bodyLayout.container().columns();



            /*
             * ----------------------------------------
             * 3. TITLE WIDTH (GRID COMPATIBLE)
             * ----------------------------------------
             */

            int titlePixelWidth =
                    MinecraftCharWidths.getPixelWidth(card.getTitle());

            int titleCols =
                    (int) Math.ceil(titlePixelWidth / 16.0);

            /*
             * ----------------------------------------
             * 4. FINAL WIDTH (GRID ALIGNED)
             * ----------------------------------------
             */

            int cols = Math.max(bodyCols, titleCols);
            int width = cols * 16;


            /*
             * ----------------------------------------
             * 5. HEADER (DERIVED FROM BODY)
             * ----------------------------------------
             */

            DrawGradientCommand gradient =
                    new DrawGradientCommand(
                            card.getX(),
                            headerY,
                            width,
                            headerHeight,
                            card.getStyle().startColor(),
                            card.getStyle().endColor(),
                            card.getStyle().gradientDirection()
                    );

            GradientRenderer renderer =
                    gradientRenderers.get(gradient.getDirection());

            cursor.seek(builder, card.getX());

            builder.append(renderer.render(gradient, headerY));

            cursor.advance(width);

            /*
             * ----------------------------------------
             * 6. TITLE (CENTERED)
             * ----------------------------------------
             */

            List<LineData> titleData =
                    List.of(new LineData(card.getTitle(), 8));

            List<StringGlyph> titleGlyphs =
                    layoutEngine.layout(titleData);

            int titleX =
                    card.getX() + ((width - titlePixelWidth) / 2);

            cursor.seek(builder, titleX);

            builder.append(
                    stringRenderer.render(
                            titleGlyphs,
                            headerY - 2
                    )
            );

            cursor.advance(titlePixelWidth + 1);

            /*
             * ----------------------------------------
             * 7. BODY (TEXT CONTAINER)
             * ----------------------------------------
             */

            DrawTextContainerCommand body =
                    new DrawTextContainerCommand(
                            card.getX(),
                            bodyY,
                            card.getDescription()
                    );

            textContainerAssembler.assemble(
                    builder,
                    cursor,
                    List.of(body)
            );




        }

    }

    /*public void assemble(
            StringBuilder builder,
            RenderCursor cursor,
            List<DrawCommand> commands
    ) {

        for (DrawCommand command : commands) {

            if (!(command instanceof DescriptionCardCommand card)) {
                continue;
            }



            List<LineData> lines = new ArrayList<>();
            for (String s : card.getDescription()) {
                lines.add(new LineData(s, 8));
            }

            TextContainerLayout.ContainerLayout bodyLayout =
                    TextContainerLayout.measure(
                            lines,
                            card.getY()
                    );

            int bodyCols = bodyLayout.container().columns();



            int titlePixelWidth =
                    MinecraftCharWidths.getPixelWidth(card.getTitle());

            int titleCols =
                    (int) Math.ceil(titlePixelWidth / 16.0);



            int cols = Math.max(bodyCols, titleCols);
            int width = cols * 16;


            int headerHeight = 10;
            int headerY = card.getY();
            int bodyY = headerY - headerHeight - 16;


            DrawGradientCommand gradient =
                    new DrawGradientCommand(
                            card.getX(),
                            headerY,
                            width,
                            headerHeight,
                            card.getStyle().startColor(),
                            card.getStyle().endColor(),
                            card.getStyle().gradientDirection()
                    );

            GradientRenderer renderer =
                    gradientRenderers.get(gradient.getDirection());

            cursor.seek(builder, card.getX());
            builder.append(renderer.render(gradient, headerY));


            cursor.advance(width);


            List<LineData> titleData =
                    List.of(new LineData(card.getTitle(), 8));

            List<StringGlyph> titleGlyphs =
                    layoutEngine.layout(titleData);

            int titleX =
                    card.getX() + ((width - titlePixelWidth) / 2);

            cursor.seek(builder, titleX);

            builder.append(
                    stringRenderer.render(
                            titleGlyphs,
                            headerY - 2
                    )
            );

            cursor.advance(titlePixelWidth+1);


            DrawTextContainerCommand body =
                    new DrawTextContainerCommand(
                            card.getX(),
                            bodyY,
                            card.getDescription()
                    );

            textContainerAssembler.assemble(
                    builder,
                    cursor,
                    List.of(body)
            );
        }
    }*/



}
