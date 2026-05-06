package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

import java.util.List;

public class StringRenderer {

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

            for (CharGlyph glyph : line.getGlyphs()) {

                //Bukkit.getLogger().info("trying to render '" + glyph.getChar() +"'");

                if (glyph.getChar() == ' ') {
                    //+3
                    sb.append("\uF823");
                    continue;
                }

                sb.append(glyph.get(currentAscent));
            }


            if(lines.getLast()!=line){
                //append negative depending on space of line
                int width = line.getWidth();

                for (int i = 0; i < PIXELS.length; i++) {

                    while (width >= PIXELS[i]) {
                        sb.append(NEGATIVE_GLYPHS[i]);
                        width -= PIXELS[i];
                    }

                }


                currentAscent-=line.getNextLineIn();
            }

        }

        Bukkit.getLogger().info("string length is " + sb.toString().length());

        return sb.toString();
    }

}
