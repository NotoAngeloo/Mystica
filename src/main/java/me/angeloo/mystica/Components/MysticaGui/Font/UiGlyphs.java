package me.angeloo.mystica.Components.MysticaGui.Font;

public class UiGlyphs {

    public static final Glyph LEFT_ARROW = new Glyph(16, 16);
    public static final Glyph RIGHT_ARROW = new Glyph(16, 16);

    //bigger glyphs take into account the empty space between slots
    public static final Glyph BUTTON_2 = new Glyph(32, 32);

    public static final Glyph EMPTY_SLOT = new Glyph(16, 16);

    static {

        EMPTY_SLOT.addVariant(0, "\uF829\uF821");
        EMPTY_SLOT.addVariant(1, "\uF829\uF821");
        EMPTY_SLOT.addVariant(2, "\uF829\uF821");
        EMPTY_SLOT.addVariant(3, "\uF829\uF821");
        EMPTY_SLOT.addVariant(4, "\uF829\uF821");
        EMPTY_SLOT.addVariant(5, "\uF829\uF821");
        EMPTY_SLOT.addVariant(6, "\uF829\uF821");
        EMPTY_SLOT.addVariant(7, "\uF829\uF821");
        EMPTY_SLOT.addVariant(8, "\uF829\uF821");
        EMPTY_SLOT.addVariant(9, "\uF829\uF821");


        LEFT_ARROW.addVariant(0, "\ue4ea");
        LEFT_ARROW.addVariant(1, "\ue4eb");
        LEFT_ARROW.addVariant(2, "\ue4ec");
        LEFT_ARROW.addVariant(3, "\ue4ed");
        LEFT_ARROW.addVariant(4, "\ue4ee");
        LEFT_ARROW.addVariant(5, "\ue4ef");
        LEFT_ARROW.addVariant(6, "\ue4f0");
        LEFT_ARROW.addVariant(7, "\ue4f1");
        LEFT_ARROW.addVariant(8, "\ue4f2");
        LEFT_ARROW.addVariant(9, "\ue4f3");

        RIGHT_ARROW.addVariant(0, "\ue4f4");
        RIGHT_ARROW.addVariant(1, "\ue4f5");
        RIGHT_ARROW.addVariant(2, "\ue4f6");
        RIGHT_ARROW.addVariant(3, "\ue4f7");
        RIGHT_ARROW.addVariant(4, "\ue4f8");
        RIGHT_ARROW.addVariant(5, "\ue4f9");
        RIGHT_ARROW.addVariant(6, "\ue4fa");
        RIGHT_ARROW.addVariant(7, "\ue4fb");
        RIGHT_ARROW.addVariant(8, "\ue4fc");
        RIGHT_ARROW.addVariant(9, "\ue4fd");

        BUTTON_2.addVariant(0, "\ue4fe");
    }

}
