package me.angeloo.mystica.Utility.TextRenderer;

public class StringGlyph {

    private final CharGlyph[] glyphs;
    private final int width;
    private final int nextLineIn;

    public StringGlyph(CharGlyph[] glyphs, int nextLineIn) {
        this.glyphs = glyphs;
        this.nextLineIn = nextLineIn;

        int sum = 0;
        for (CharGlyph g : glyphs) {
            sum += g.getWidth();
        }

        this.width = sum;
    }

    public CharGlyph[] getGlyphs() {
        return glyphs;
    }

    public int getWidth() {
        return width;
    }

    public int getNextLineIn() { return nextLineIn; }


}
