package me.angeloo.mystica.Components.MysticaGui.Font;

public class UiGlyphs {

    public static final Glyph LEFT_ARROW = new Glyph(16, 16, 1);
    public static final Glyph RIGHT_ARROW = new Glyph(16, 16, 1);
    public static final Glyph UP_ARROW = new Glyph(16, 16, 1);
    public static final Glyph DOWN_ARROW = new Glyph(16, 16, 1);

    public static final Glyph BUTTON_1 = new Glyph(16, 16, 1);
    //bigger glyphs take into account the empty space between slots
    public static final Glyph BUTTON_2x2 = new Glyph(34, 34, 2);
    public static final Glyph BUTTON_3x3 = new Glyph(52, 52, 3);

    public static final Glyph BUTTON_1x3 = new Glyph(52, 16, 3);

    public static final Glyph DEFAULT_BACKGROUND = new Glyph(768, 768, 0);

    public static Glyph DUELIST_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph ALCHEMIST_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph PYROMANCER_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph CONJURER_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph SHEPARD_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph ARCANE_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph TEMPLAR_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph DAWN_ICON_2 = new Glyph(34,34, 2);
    public static Glyph SCOUT_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph TAMER_ICON_2 = new Glyph(34,34, 2);
    public static Glyph DOOM_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph BLOOD_ICON_2 = new Glyph(34, 34, 2);
    public static Glyph GLADIATOR_ICON_2 = new Glyph(34, 34,2);
    public static Glyph EXECUTIONER_ICON_2 = new Glyph(34, 34, 2);

    public static Glyph CLASS_ART = new Glyph(52, 106, 3);
    public static Glyph ASSASSIN_CHARACTER = new Glyph(52, 106, 3);
    public static Glyph ELEMENTALIST_CHARACTER = new Glyph(52, 106, 3);
    public static Glyph MYSTIC_CHARACTER = new Glyph(52, 106, 3);
    public static Glyph PALADIN_CHARACTER = new Glyph(52, 106, 3);
    public static Glyph RANGER_CHARACTER = new Glyph(52, 106, 3);
    public static Glyph WARRIOR_CHARACTER = new Glyph(52, 106, 3);
    public static Glyph SHADOW_KNIGHT_CHARACTER = new Glyph(52, 106, 3);

    public static Glyph ASSASSIN_ICON = new Glyph(34,34,2);
    public static Glyph ELEMENTALIST_ICON = new Glyph(34,34,2);
    public static Glyph MYSTIC_ICON = new Glyph(34,34,2);
    public static Glyph PALADIN_ICON = new Glyph(34,34,2);
    public static Glyph RANGER_ICON = new Glyph(34,34,2);
    public static Glyph SHADOW_KNIGHT_ICON = new Glyph(34,34,2);
    public static Glyph WARRIOR_ICON = new Glyph(34,34,2);

    public static Glyph ITEM_BANNER_RARE = new Glyph(148, 40, 8);
    public static Glyph ITEM_TOOLTIP_BACKGROUND = new Glyph(148, 164, 8);

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

        UP_ARROW.addVariant(0, "\ue518");
        UP_ARROW.addVariant(1, "\ue519");
        UP_ARROW.addVariant(2, "\ue51a");
        UP_ARROW.addVariant(3, "\ue51b");
        UP_ARROW.addVariant(4, "\ue51c");
        UP_ARROW.addVariant(5, "\ue51d");
        UP_ARROW.addVariant(6, "\ue51e");
        UP_ARROW.addVariant(7, "\ue51f");
        UP_ARROW.addVariant(8, "\ue520");
        UP_ARROW.addVariant(9, "\ue521");

        DOWN_ARROW.addVariant(0, "\ue522");
        DOWN_ARROW.addVariant(1, "\ue523");
        DOWN_ARROW.addVariant(2, "\ue524");
        DOWN_ARROW.addVariant(3, "\ue525");
        DOWN_ARROW.addVariant(4, "\ue526");
        DOWN_ARROW.addVariant(5, "\ue527");
        DOWN_ARROW.addVariant(6, "\ue528");
        DOWN_ARROW.addVariant(7, "\ue529");
        DOWN_ARROW.addVariant(8, "\ue52a");
        DOWN_ARROW.addVariant(9, "\ue52b");

        PYROMANCER_ICON_2.addVariant(0, "\ue501");
        CONJURER_ICON_2.addVariant(2, "\ue502");
        DUELIST_ICON_2.addVariant(0, "\ue53d");
        ALCHEMIST_ICON_2.addVariant(2, "\ue53e");
        SHEPARD_ICON_2.addVariant(0, "\ue53f");
        ARCANE_ICON_2.addVariant(2, "\ue540");
        TEMPLAR_ICON_2.addVariant(0, "\ue541");
        DAWN_ICON_2.addVariant(2, "\ue542");
        SCOUT_ICON_2.addVariant(0, "\ue543");
        TAMER_ICON_2.addVariant(2, "\ue544");
        DOOM_ICON_2.addVariant(0, "\ue545");
        BLOOD_ICON_2.addVariant(2, "\ue546");
        GLADIATOR_ICON_2.addVariant(0, "\ue547");
        EXECUTIONER_ICON_2.addVariant(2, "\ue548");

        BUTTON_1.addVariant(0, "\ue50e");
        BUTTON_1.addVariant(1, "\ue50f");
        BUTTON_1.addVariant(2, "\ue510");
        BUTTON_1.addVariant(3, "\ue511");
        BUTTON_1.addVariant(4, "\ue512");
        BUTTON_1.addVariant(5, "\ue513");
        BUTTON_1.addVariant(6, "\ue514");
        BUTTON_1.addVariant(7, "\ue515");
        BUTTON_1.addVariant(8, "\ue516");
        BUTTON_1.addVariant(9, "\ue517");

        BUTTON_2x2.addVariant(0, "\ue4fe");
        BUTTON_2x2.addVariant(1, "\ue503");
        BUTTON_2x2.addVariant(2, "\ue504");
        BUTTON_2x2.addVariant(3, "\ue505");
        BUTTON_2x2.addVariant(4, "\ue506");
        //cannot be at 5 because would be in the gap
        BUTTON_2x2.addVariant(6, "\ue507");
        BUTTON_2x2.addVariant(7, "\ue508");
        //cannot be at 8 or 9

        BUTTON_3x3.addVariant(0, "\ue4ff");
        BUTTON_3x3.addVariant(1, "\ue509");
        BUTTON_3x3.addVariant(2, "\ue50a");
        BUTTON_3x3.addVariant(3, "\ue50b");
        //cannot be at 4, 5
        BUTTON_3x3.addVariant(6, "\ue50c");
        //cannot be at 7,8, 9

        BUTTON_1x3.addVariant(9, "\ue52e");

        CLASS_ART.addVariant(0, "\ue50d");
        ASSASSIN_CHARACTER.addVariant(0, "\ue537");
        ELEMENTALIST_CHARACTER.addVariant(0, "\ue52f");
        MYSTIC_CHARACTER.addVariant(0, "\ue538");
        PALADIN_CHARACTER.addVariant(0, "\ue539");
        RANGER_CHARACTER.addVariant(0, "\ue53a");
        SHADOW_KNIGHT_CHARACTER.addVariant(0, "\ue53b");
        WARRIOR_CHARACTER.addVariant(0, "\ue53c");

        ASSASSIN_ICON.addVariant(6, "\ue530");
        ELEMENTALIST_ICON.addVariant(6, "\ue531");
        MYSTIC_ICON.addVariant(6, "\ue532");
        PALADIN_ICON.addVariant(6, "\ue533");
        RANGER_ICON.addVariant(6, "\ue534");
        SHADOW_KNIGHT_ICON.addVariant(6, "\ue535");
        WARRIOR_ICON.addVariant(6, "\ue536");

        ITEM_BANNER_RARE.addVariant(0, "\ue52c");
        ITEM_TOOLTIP_BACKGROUND.addVariant(0, "\ue52d");

        DEFAULT_BACKGROUND.addVariant(0, "\ue500");
    }

}
