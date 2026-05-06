package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class PixelGlyphRegistry {

    private static final int START_ASCENT = 1;
    private static final int END_ASCENT   = -356;
    private static final int BASE_UNICODE = 0xE242;

    //-356 is more than enough

    private final Map<Integer, PixelGlyph> ascentMap = new HashMap<>();

    public PixelGlyphRegistry() {
        int index = 0;

        for (int ascent = START_ASCENT; ascent >= END_ASCENT; ascent--) {
            char unicode = (char) (BASE_UNICODE + index);

            ascentMap.put(ascent, new PixelGlyph(unicode));

            index++;
        }

        //Bukkit.getLogger().info("Pixel Glyphs Registered");
    }


    public PixelGlyph get(int ascent) {
        return ascentMap.get(ascent);
    }

}
