package me.angeloo.mystica.Utility.ShapeRenderer.Text;

import me.angeloo.mystica.Utility.ShapeRenderer.Text.CharGlyph;

public class RenderGlyph {

    private final CharGlyph glyph;

    private final String formatting;

    public RenderGlyph(
            CharGlyph glyph,
            String formatting
    ) {

        this.glyph = glyph;
        this.formatting = formatting;
    }

    public CharGlyph glyph() {
        return glyph;
    }

    public String formatting() {
        return formatting;
    }

    public int getWidth() {
        return glyph.getWidth();
    }

}
