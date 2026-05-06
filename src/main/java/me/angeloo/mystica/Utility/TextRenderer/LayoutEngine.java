package me.angeloo.mystica.Utility.TextRenderer;

import java.util.ArrayList;
import java.util.List;

public class LayoutEngine {

    private final CharGlyphAtlas atlas;

    public LayoutEngine(CharGlyphAtlas atlas) {
        this.atlas = atlas;
    }

    public List<StringGlyph> layout(List<LineData> dataList) {

        List<StringGlyph> result = new ArrayList<>();

        for (LineData data : dataList) {

            CharGlyph[] glyphs = buildGlyphs(data.text());

            result.add(new StringGlyph(
                    glyphs,
                    data.offset()
            ));
        }

        return result;
    }

    private CharGlyph[] buildGlyphs(String text) {

        List<CharGlyph> out = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            CharGlyph g = atlas.get(c);
            if (g != null) out.add(g);
        }

        return out.toArray(new CharGlyph[0]);
    }


}
