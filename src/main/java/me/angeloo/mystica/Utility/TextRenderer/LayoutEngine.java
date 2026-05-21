package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class LayoutEngine {

    private final CharGlyphAtlas atlas;

    public LayoutEngine(CharGlyphAtlas atlas) {
        this.atlas = atlas;
    }

    public List<StringGlyph> layout(
            List<LineData> dataList
    ) {

        List<StringGlyph> result =
                new ArrayList<>();

        for(LineData data : dataList) {

            RenderGlyph[] glyphs = buildGlyphs(data.text());

            result.add(
                    new StringGlyph(
                            glyphs,
                            data.nextLineOffset()
                    )
            );
        }

        return result;
    }


    private RenderGlyph[] buildGlyphs(String text) {

        List<RenderGlyph> out =
                new ArrayList<>();

        String activeFormat = "";

        for(int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            /*
             * Detect formatting code
             */

            if(c == '§' && i + 1 < text.length()) {

                activeFormat =
                        "§" + text.charAt(i + 1);

                i++;

                continue;
            }

            CharGlyph glyph = atlas.get(c);

            if(glyph != null) {

                out.add(
                        new RenderGlyph(
                                glyph,
                                activeFormat
                        )
                );
            }
        }

        return out.toArray(
                new RenderGlyph[0]
        );
    }



}
