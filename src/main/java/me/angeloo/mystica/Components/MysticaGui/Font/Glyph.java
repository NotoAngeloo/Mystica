package me.angeloo.mystica.Components.MysticaGui.Font;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Glyph {

    private final int width;
    private final int height;

    private final int slotWidth;

    private final Map<Integer, GlyphVariant>
            variants = new HashMap<>();

    public Glyph(
            int width,
            int height,
            int slotWidth
    ) {

        this.width = width;
        this.height = height;
        this.slotWidth = slotWidth;
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

    public int slotWidth(){
        return slotWidth;
    }


}
