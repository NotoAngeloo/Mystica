package me.angeloo.mystica.Components.MysticaGui.Assemble;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawCommand;
import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.GlyphVariant;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.Font.UiSpacing;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderCursor;

import java.util.Arrays;
import java.util.List;


public class ButtonLayerAssembler {

    private static final int
            INVENTORY_SIZE = 90;

    private static final int
            INVENTORY_COLUMNS = 9;

    private static final int
            INVENTORY_ROWS = 10;

    public void assemble(
            StringBuilder builder,
            RenderCursor cursor,
            List<DrawCommand> commands
    ) {

        Glyph[] buffer = createBuffer();

        applyCommands(
                buffer,
                commands
        );

        renderBuffer(
                builder,
                buffer
        );

        cursor.advance(162);
    }

    /*
     * ----------------------------------------
     * BUFFER CREATION
     * ----------------------------------------
     */

    private Glyph[] createBuffer() {

        Glyph[] buffer =
                new Glyph[INVENTORY_SIZE];

        Arrays.fill(
                buffer,
                UiGlyphs.EMPTY_SLOT
        );

        return buffer;
    }

    /*
     * ----------------------------------------
     * APPLY COMMANDS
     * ----------------------------------------
     */

    private void applyCommands(
            Glyph[] buffer,
            List<DrawCommand> commands
    ) {

        for(DrawCommand command
                : commands) {

            if(!(command
                    instanceof DrawSlotCommand icon)) {

                continue;
            }

            int slot = icon.getSlot();

            if(!isValidSlot(slot)) {

                continue;
            }

            buffer[slot] = icon.glyph();
        }
    }

    /*
     * ----------------------------------------
     * RENDER BUFFER
     * ----------------------------------------
     */

    private void renderBuffer(
            StringBuilder builder,
            Glyph[] buffer
    ) {

        for(int row = 0;
            row < INVENTORY_ROWS;
            row++) {

            renderRow(
                    builder,
                    buffer,
                    row
            );

            appendRowSpacing(
                    builder,
                    row
            );
        }
    }

    private void renderRow(
            StringBuilder builder,
            Glyph[] buffer,
            int row
    ) {

        int col = 0;

        while(col < INVENTORY_COLUMNS) {

            /*
             * Resolve slot index
             */

            int slot =
                    row * INVENTORY_COLUMNS + col;

            /*
             * Resolve glyph
             */

            Glyph glyph =
                    buffer[slot];

            if(glyph == null) {

                glyph =
                        UiGlyphs.EMPTY_SLOT;
            }

            /*
             * Resolve variant
             */

            GlyphVariant variant =
                    glyph.getVariant(row);

            if(variant == null) {

                variant =
                        UiGlyphs.EMPTY_SLOT
                                .getVariant(row);
            }

            if(variant == null) {

                col++;
                continue;
            }

            /*
             * Render glyph
             */

            builder.append(
                    variant.unicode()
            );


            /*
             * Determine how many slots this glyph occupies
             */


            /*
             * IMPORTANT:
             * advance column by glyph width
             */

            col += glyph.slotWidth();

            /*
             * Only add spacing if we are not at row end
             */

            builder.append(UiSpacing.SLOT_GAP);
        }
    }


    /*
     * ----------------------------------------
     * ROW SPACING
     * ----------------------------------------
     */

    private void appendRowSpacing(
            StringBuilder builder,
            int row
    ) {

        /*
         * Final row does not
         * need spacing
         */

        if(row >= INVENTORY_ROWS - 1) {

            return;
        }

        builder.append(
                UiSpacing.RETURN_TO_ROW_START
        );

    }

    /*
     * ----------------------------------------
     * HELPERS
     * ----------------------------------------
     */

    private boolean isValidSlot(
            int slot
    ) {

        return slot >= 0 &&
                slot < INVENTORY_SIZE;
    }

}
