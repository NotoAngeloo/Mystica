package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class CharGlyph {

    private final PixelMatrix matrix;
    private final int width;

    // cache per ascent
    private final Map<Integer, String> rendered = new HashMap<>();

    private final String[] ADVANCE_GLYPH = {"","\uF821","\uF822","\uF823","\uF824","\uF825","\uF826","\uF827","\uF828"};
    private final String[] REVERSE_GLYPH = {"","\uF801","\uF802","\uF803","\uF804","\uF805","\uF806","\uF807","\uF808"};

    public CharGlyph(PixelMatrix matrix) {
        this.matrix = matrix;
        this.width = matrix.getWidth();
    }

    public int getWidth() {
        return width;
    }

    public void precompute(PixelGlyphRegistry registry, int startAscent, int endAscent) {
        for (int ascent = startAscent; ascent >= endAscent; ascent--) {
            rendered.put(ascent, render(matrix, registry, ascent));
        }
    }

    public String get(int ascent) {
        return rendered.getOrDefault(ascent, "");
    }

    // your existing render logic goes here
    private String render(PixelMatrix matrix, PixelGlyphRegistry registry, int baseAscent) {

        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < 8; y++) {

            int lastFilled = -1;

            for (int x = 7; x >= 0; x--) {
                if (matrix.isFilled(x, y)) {
                    lastFilled = x;
                    break;
                }
            }

            if (lastFilled == -1) continue;

            int x = 0;
            int spaces = 0;

            while (x <= lastFilled) {

                if (!matrix.isFilled(x, y)) {
                    spaces++;
                    x++;
                    continue;
                }

                // 4. flush spaces BEFORE drawing pixel
                if (spaces > 0) {
                    sb.append(ADVANCE_GLYPH[spaces]);
                    spaces = 0;
                }

                int ascent = baseAscent - y;

                //Bukkit.getLogger().info("trying to get unicode for ascent "+ ascent);

                sb.append(registry.get(ascent).getUnicode());
                sb.append("\uF801");

                x++;
            }

            // 6. track width
            int currentWidth = lastFilled + 1;
            // 7. reset X for next row
            if (y != 7) {
                sb.append(REVERSE_GLYPH[currentWidth]);
                //append current width

            }
        }

        return sb.toString();
    }
}
