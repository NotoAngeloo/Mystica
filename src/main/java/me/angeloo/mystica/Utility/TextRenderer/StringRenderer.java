package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringRenderer {

    private final Map<String, Integer> widthMap = new HashMap<>();

    private static final int[] PIXELS = {64, 32, 16, 8, 4, 2, 1};

    private static final String[] NEGATIVE_GLYPHS = {
            "\uF80B",
            "\uF80A",
            "\uF809",
            "\uF808",
            "\uF804",
            "\uF802",
            "\uF801"
    };

    public StringRenderer(){
    }


    public String render(List<StringGlyph> lines, int baseAscent) {

        if(baseAscent>1){
            baseAscent=1;
        }

        StringBuilder sb = new StringBuilder();

        int currentAscent = baseAscent;

        for (StringGlyph line : lines) {

            String activeFormatting = "";

            for(RenderGlyph renderGlyph : line.getGlyphs()) {

                CharGlyph glyph = renderGlyph.glyph();

                /*
                 * Only emit formatting when changed
                 */

                if(!renderGlyph.formatting().equals(activeFormatting)) {

                    sb.append(renderGlyph.formatting());

                    activeFormatting = renderGlyph.formatting();
                }

                if(glyph.getChar() == ' ') {

                    sb.append("\uF823");
                    continue;
                }

                sb.append(glyph.get(currentAscent));
            }

            int width = line.getWidth();
            widthMap.put(sb.toString(), width);

            if(lines.getLast()!=line){
                //append negative depending on space of line

                for (int i = 0; i < PIXELS.length; i++) {

                    while (width >= PIXELS[i]) {
                        sb.append(NEGATIVE_GLYPHS[i]);
                        width -= PIXELS[i];
                    }

                }

                currentAscent-=line.getNextLineIn();
            }

        }

        //Bukkit.getLogger().info("string length is " + sb.toString().length());

        return sb.toString();
    }

    public int getWidth(String text){

        return widthMap.getOrDefault(text, 0);
    }

}
