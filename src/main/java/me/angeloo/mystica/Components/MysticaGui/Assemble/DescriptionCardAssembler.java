package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DescriptionCardCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.ContainerCommand.DrawTextContainerCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawConstructedIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.DrawIconCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.GradientCommand.DrawGradientCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.MinecraftCharWidths;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderer;
import me.angeloo.mystica.Utility.ShapeRenderer.Gradient.GradientRenderers;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LayoutEngine;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.LineData;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringGlyph;
import me.angeloo.mystica.Utility.ShapeRenderer.Text.StringRenderer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class DescriptionCardAssembler {

    private final GradientRenderers gradientRenderers;
    private final LayoutEngine layoutEngine;
    private final StringRenderer stringRenderer;
    private final TextContainerAssembler textContainerAssembler;
    private final IconLayerAssembler iconLayerAssembler;

    public DescriptionCardAssembler(Mystica main, TextContainerAssembler textContainerAssembler, IconLayerAssembler iconLayerAssembler) {
        gradientRenderers = main.getGradientRenderers();
        layoutEngine = main.getLayoutEngine();
        stringRenderer = main.getStringRenderer();
        this.textContainerAssembler = textContainerAssembler;
        this.iconLayerAssembler = iconLayerAssembler;
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
             * 1. SAFE BODY ANCHOR
             * ----------------------------------------
             */

            int bodyY = (card.y() / 16) * 16 - 16;

            /*
             * ----------------------------------------
             * 2. BODY LAYOUT
             * ----------------------------------------
             */

            List<LineData> lines = new ArrayList<>();
            for (String s : card.description()) {
                lines.add(new LineData(s, 8));
            }

            TextContainerLayout.ContainerLayout bodyLayout =
                    TextContainerLayout.measure(lines, bodyY);

            int bodyCols = bodyLayout.container().columns();

            /*
             * ----------------------------------------
             * 3. TITLE WIDTH
             * ----------------------------------------
             */

            int titlePixelWidth = 0;
            for (String l : card.title()) {
                titlePixelWidth = Math.max(
                        titlePixelWidth,
                        MinecraftCharWidths.getPixelWidth(l)
                );
            }

            int titleCols = (int) Math.ceil(titlePixelWidth / 16.0);

            /*
             * ----------------------------------------
             * 4. ICON + TITLE HEIGHT LOGIC
             * ----------------------------------------
             */

            int lineHeight = 8;

            int titleHeight = card.title().size() * lineHeight;

            int iconWidth = card.icon() == null ? 0 : card.icon().width();
            int iconHeight = card.icon() == null ? 0 : card.icon().height();


            int contentHeight = Math.max(titleHeight, iconHeight);

            int headerHeight = contentHeight + 4; //+2 top, +2 bottom
            int headerTopY = bodyY + headerHeight + 3;

            /*
             * ----------------------------------------
             * 5. WIDTH (UNCHANGED LOGIC)
             * ----------------------------------------
             */

            int cols = Math.max(bodyCols, titleCols);
            int width = cols * 16;

            /*
             * ----------------------------------------
             * 6. HEADER POSITION
             * ----------------------------------------
             */


            /*
             * ----------------------------------------
             * 7. GRADIENT
             * ----------------------------------------
             */

            DrawGradientCommand gradient =
                    new DrawGradientCommand(
                            card.x(),
                            headerTopY,
                            width,
                            headerHeight,
                            card.style().startColor(),
                            card.style().endColor(),
                            card.style().gradientDirection()
                    );

            GradientRenderer renderer =
                    gradientRenderers.get(gradient.direction());

            cursor.seek(builder, card.x());
            builder.append(renderer.render(gradient, headerTopY));
            cursor.advance(width);

            //each line of text makes the 9 slice offset by 1

            /*
             * ----------------------------------------
             * 8. TITLE
             * ----------------------------------------
             */

            List<LineData> titleData = new ArrayList<>();
            for (String l : card.title()) {
                titleData.add(new LineData(l, 8));
            }

            List<StringGlyph> titleGlyphs =
                    layoutEngine.layout(titleData);

            int iconOffset = 2;
            int iconGap = 2;

            int iconX = card.x() + iconOffset;
            int iconY = headerTopY - 2; //2 below top header

            int titleX = card.x() + iconOffset;

            if (card.icon() != null) {
                titleX += iconWidth + iconGap;
            }

            cursor.seek(builder, titleX);

            builder.append(
                    stringRenderer.render(
                            titleGlyphs,
                            iconY
                    )
            );

            int lastLineWidth = MinecraftCharWidths.getPixelWidth(card.title().getLast());

            cursor.advance(lastLineWidth+1);

            /*
             * ----------------------------------------
             * 9. ICON
             * ----------------------------------------
             */

            if(card.icon() != null){
                DrawConstructedIconCommand iconCommand = new DrawConstructedIconCommand(
                        iconX,
                        iconY,
                        card.icon());

                iconLayerAssembler.assemble(builder, cursor, List.of(iconCommand));
            }



            /*
             * ----------------------------------------
             * 10. BODY
             * ----------------------------------------
             */

            //perhaps container width can also be effected by how wide the title is

            DrawTextContainerCommand body =
                    new DrawTextContainerCommand(
                            card.x(),
                            bodyY,
                            card.description()
                    );

            textContainerAssembler.assemble(
                    builder,
                    cursor,
                    List.of(body)
            );
        }
    }




}
