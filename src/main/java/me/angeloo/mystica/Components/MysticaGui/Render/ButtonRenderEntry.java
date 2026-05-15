package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;

public class ButtonRenderEntry {

    private final Glyph glyph;

    private final int ascent;

    public ButtonRenderEntry(
            Glyph glyph,
            int ascent
    ) {

        this.glyph = glyph;
        this.ascent = ascent;
    }

    public Glyph glyph() {

        return glyph;
    }

    public int ascent() {

        return ascent;
    }

}
