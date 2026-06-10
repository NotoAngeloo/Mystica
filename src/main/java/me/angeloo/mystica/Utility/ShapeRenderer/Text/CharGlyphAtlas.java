package me.angeloo.mystica.Utility.ShapeRenderer.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CharGlyphAtlas {

    private final Map<Character, CharGlyph> glyphs = new HashMap<>();

    public void register(char c, PixelMatrix matrix) {
        glyphs.put(c, new CharGlyph(matrix));
    }

    public CharGlyph get(char c) {
        return glyphs.get(c);
    }

    public Collection<CharGlyph> all() {
        return glyphs.values();
    }

}
