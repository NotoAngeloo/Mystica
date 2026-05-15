package me.angeloo.mystica.Components.MysticaGui.Font;

public class UiGlyphs {

    public static final Glyph LEFT_ARROW = new Glyph(16, 16);

    public static final Glyph EMPTY_SLOT = new Glyph(16, 16);

    static {

        EMPTY_SLOT.addVariant(0, "\uF829\uF821");
        EMPTY_SLOT.addVariant(1, "\uF829\uF821");
        EMPTY_SLOT.addVariant(2, "\uF829\uF821");
        EMPTY_SLOT.addVariant(3, "\uF829\uF821");
        EMPTY_SLOT.addVariant(4, "\uF829\uF821");
        EMPTY_SLOT.addVariant(5, "\uF829\uF821");


        LEFT_ARROW.addVariant(0, "\ue4ea");
        LEFT_ARROW.addVariant(1, "\ue4eb");
        LEFT_ARROW.addVariant(2, "\ue4ec");
        LEFT_ARROW.addVariant(3, "\ue4ed");
        LEFT_ARROW.addVariant(4, "\ue4ee");
        LEFT_ARROW.addVariant(5, "\ue4ef");
    }

}
