package me.angeloo.mystica.Components.MysticaGui.Font;

public class GlyphVariant {

    private final String unicode;

    private final int ascent;

    public GlyphVariant(
            String unicode,
            int ascent
    ) {

        this.unicode = unicode;
        this.ascent = ascent;
    }

    public String unicode() {

        return unicode;
    }

    public int ascent() {

        return ascent;
    }
}