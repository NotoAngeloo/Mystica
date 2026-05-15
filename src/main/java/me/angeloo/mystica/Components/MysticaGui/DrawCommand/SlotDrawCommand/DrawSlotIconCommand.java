package me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

public class DrawSlotIconCommand implements SlotDrawCommand{

    private final int slot;

    private final Glyph glyph;

    public DrawSlotIconCommand(
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
