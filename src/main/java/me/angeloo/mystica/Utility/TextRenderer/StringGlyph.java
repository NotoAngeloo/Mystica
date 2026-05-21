package me.angeloo.mystica.Utility.TextRenderer;

public class StringGlyph {

    private final RenderGlyph[] glyphs;
    private final int width;
    private final int nextLineIn;

    public StringGlyph(RenderGlyph[] glyphs, int nextLineIn) {
        this.glyphs = glyphs;
        this.nextLineIn = nextLineIn;

        int sum = 0;
        for (RenderGlyph g : glyphs) {
            sum += g.getWidth();
        }

        this.width = sum;
    }

    public RenderGlyph[] getGlyphs() {
        return glyphs;
    }

    public int getWidth() {
        return width;
    }

    public int getNextLineIn() { return nextLineIn; }


}
