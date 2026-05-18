package me.angeloo.mystica.Components.MysticaGui.Font;

import java.util.HashMap;
import java.util.Map;

public class Glyph {

    private final int width;

    private final int height;

    private final Map<Integer, GlyphVariant>
            variants = new HashMap<>();

    public Glyph(
            int width,
            int height
    ) {

        this.width = width;
        this.height = height;
    }

    public void addVariant(
            int ascent,
            String unicode
    ) {

        variants.put(
                ascent,
                new GlyphVariant(
                        unicode,
                        ascent
                )
        );
    }

    public GlyphVariant getVariant(
            int ascent
    ) {

        return variants.get(ascent);
    }

    public int width(){
        return width;
    }


    public int height() {

        return height;
    }
}
