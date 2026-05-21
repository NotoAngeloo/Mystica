package me.angeloo.mystica.Components.MysticaGui.Font;

public class UiGlyphs {

    public static final Glyph LEFT_ARROW = new Glyph(16, 16, 1);
    public static final Glyph RIGHT_ARROW = new Glyph(16, 16, 1);

    //bigger glyphs take into account the empty space between slots
    public static final Glyph BUTTON_2 = new Glyph(34, 34, 2);
    public static final Glyph BUTTON_3 = new Glyph(52, 52, 3);

    public static final Glyph DEFAULT_BACKGROUND = new Glyph(768, 768, 0);

    public static Glyph PYROMANCER_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph CONJURER_ICON_2 = new Glyph(34, 34, 2);

    public static Glyph CLASS_ART = new Glyph(52, 106, 3);

    public static final Glyph EMPTY_SLOT = new Glyph(16, 16, 1);

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

        PYROMANCER_ICON_2.addVariant(0, "\ue501");
        CONJURER_ICON_2.addVariant(2, "\ue502");

        BUTTON_2.addVariant(0, "\ue4fe");
        BUTTON_2.addVariant(1, "\ue503");
        BUTTON_2.addVariant(2, "\ue504");
        BUTTON_2.addVariant(3, "\ue505");
        BUTTON_2.addVariant(4, "\ue506");
        //cannot be at 5 because would be in the gap
        BUTTON_2.addVariant(6, "\ue507");
        BUTTON_2.addVariant(7, "\ue508");
        //cannot be at 8 or 9

        BUTTON_3.addVariant(0, "\ue4ff");
        BUTTON_3.addVariant(1, "\ue509");
        BUTTON_3.addVariant(2, "\ue50a");
        BUTTON_3.addVariant(3, "\ue50b");
        //cannot be at 4, 5
        BUTTON_3.addVariant(6, "\ue50c");
        //cannot be at 7,8, 9


        CLASS_ART.addVariant(0, "\ue50d");
        //backgrounds

        DEFAULT_BACKGROUND.addVariant(0, "\ue500");
    }

}
