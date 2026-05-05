package me.angeloo.mystica.Utility.TextRenderer;

public class CharacterRenderer {

    private final PixelGlyphRegistry registry;

    public CharacterRenderer(){
        this.registry = new PixelGlyphRegistry();
    }


    public String drawA(int baseAscent) {

        //cannot go above 1
        baseAscent = Math.min(1, baseAscent);
        //or below -356, but -356+8 = -348
        baseAscent = Math.max(-348, baseAscent);

        StringBuilder sb = new StringBuilder();

        PixelMatrix a = new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01110000,
                (byte)0b00001000,
                (byte)0b01111000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00000000
        });

        for (int y = 0; y < 8; y++) {

            boolean empty = true;

            for (int x = 0; x < 8; x++) {
                if (a.isFilled(x, y)) {
                    empty = false;
                    break;
                }
            }

            if (empty) continue;

            for (int x = 0; x < 8; x++) {

                if (a.isFilled(x, y)) {

                    int ascent = baseAscent - y;

                    sb.append(registry.get(ascent).getUnicode());
                    sb.append("\uF801");
                }
                else {
                    sb.append("\uF821");
                }
            }

            if (y != 7) {
                sb.append("\uF808");
            }
        }

        return sb.toString();
    }




}
