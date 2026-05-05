package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

public class CharGlyphPreComputer {

    private static final int START_ASCENT = 1;
    //because chars are 8 tall, end ascent cannot go past
    private static final int END_ASCENT   = -356 + 8;

    public static void precomputeAll(
            CharGlyphAtlas atlas,
            PixelGlyphRegistry registry
    ) {

        for (CharGlyph glyph : atlas.all()) {
            glyph.precompute(registry, START_ASCENT, END_ASCENT);
        }
    }

}
