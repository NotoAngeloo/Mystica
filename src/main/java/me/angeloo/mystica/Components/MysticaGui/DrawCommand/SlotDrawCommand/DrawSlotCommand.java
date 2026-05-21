package me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

public class DrawSlotCommand implements SlotDrawCommand{

    private final int slot;

    private final Glyph glyph;

    public DrawSlotCommand(
            int slot,
            Glyph glyph
    ) {

        this.slot = slot;
        this.glyph = glyph;
    }

    @Override
    public int getSlot() {

        return slot;
    }

    public Glyph glyph() {

        return glyph;
    }
}
