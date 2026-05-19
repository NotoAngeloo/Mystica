package me.angeloo.mystica.Components.MysticaGui.Font;

import java.util.*;

public final class UiSpacing {

    private UiSpacing() {

    }

    /*
     * Move cursor back to
     * beginning of row
     */

    //-128, -34
    public static final String RETURN_TO_ROW_START = "\uF80C\uF80A\uF802";

    //+1
    public static final String SLOT_GAP = "\uF821";

    private static final int[] STEPS = {
            128, 64, 32, 16, 8, 4, 2, 1
    };

    private static final Map<Integer, String> OFFSETS_POSITIVE =
            Map.of(
                    128, "\uF82C",
                    64,  "\uF82B",
                    32,  "\uF82A",
                    16,  "\uF829",
                    8,   "\uF828",
                    4,   "\uF824",
                    2,   "\uF822",
                    1,   "\uF821"
            );

    private static final Map<Integer, String> OFFSETS_NEGATIVE =
            Map.of(
                    128, "\uF80C",
                    64,  "\uF80B",
                    32,  "\uF80A",
                    16,  "\uF809",
                    8,   "\uF808",
                    4,   "\uF804",
                    2,   "\uF802",
                    1,   "\uF801"
            );

    public static String offset(int amount) {

        if(amount == 0) {
            return "";
        }

        StringBuilder builder =
                new StringBuilder();

        Map<Integer, String> glyphs =
                amount < 0
                        ? OFFSETS_NEGATIVE
                        : OFFSETS_POSITIVE;

        int remaining =
                Math.abs(amount);

        for(int step : STEPS) {

            while(remaining >= step) {

                builder.append(
                        glyphs.get(step)
                );

                remaining -= step;
            }
        }

        return builder.toString();
    }


}