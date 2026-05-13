package me.angeloo.mystica.Components.Hud.DamageIndicator;

public class DamageHudRenderer {

    //+32
    private static final String EMPTY = "\uF82A";

    private static final int ROWS = 5;
    private static final int COLUMNS = 5;

    private static final int SLOT_WIDTH = 32;

    private final int[][] layout = {
            {-1, -1, 10, -1, -1},
            {-1,  6,  3,  5, -1},
            {11,  2,  0,  1, 9},
            {-1,  7,  4,  8, -1},
            {-1, -1, 12, -1, -1}
    };


    private final String NEG_1PX = "\uF801";
    private final String NEG_2PX = "\uF802";
    private final String NEG_4PX = "\uF804";

    private final String POS_1PX = "\uF821";
    private final String POS_2PX = "\uF822";
    private final String POS_4PX = "\uF824";

    private final String[][] settleDigits = {

            // Row 0
            {
                    "\ue440", "\ue441", "\ue442",
                    "\ue443", "\ue444", "\ue445",
                    "\ue446", "\ue447", "\ue448",
                    "\ue449"
            },

            // Row 1
            {
                    "\ue44a", "\ue44b", "\ue44c",
                    "\ue44d", "\ue44e", "\ue44f",
                    "\ue450", "\ue451", "\ue452",
                    "\ue453"
            },

            // Row 2
            {
                    "\ue454", "\ue455", "\ue456",
                    "\ue457", "\ue458", "\ue459",
                    "\ue45a", "\ue45b", "\ue45c",
                    "\ue45d"
            },

            // Row 3
            {
                    "\ue45e", "\ue45f", "\ue460",
                    "\ue461", "\ue462", "\ue463",
                    "\ue464", "\ue465", "\ue466",
                    "\ue467"
            },

            // Row 3
            {
                    "\ue468", "\ue469", "\ue46a",
                    "\ue46b", "\ue46c", "\ue46d",
                    "\ue46e", "\ue46f", "\ue470",
                    "\ue471"
            }
    };

    private final String[][] popDigits = {

            // Row 0
            {
                    "\ue477", "\ue478", "\ue479",
                    "\ue47a", "\ue47b", "\ue47c",
                    "\ue47d", "\ue47e", "\ue47f",
                    "\ue480"
            },

            // Row 1
            {
                    "\ue481", "\ue482", "\ue483",
                    "\ue484", "\ue485", "\ue486",
                    "\ue487", "\ue488", "\ue489",
                    "\ue48a"
            },

            // Row 2
            {
                    "\ue48b", "\ue48c", "\ue48d",
                    "\ue48e", "\ue48f", "\ue490",
                    "\ue491", "\ue492", "\ue493",
                    "\ue494"
            },

            // Row 3
            {
                    "\ue495", "\ue496", "\ue497",
                    "\ue498", "\ue499", "\ue49a",
                    "\ue49b", "\ue49c", "\ue49d",
                    "\ue49e"
            },

            // Row 3
            {
                    "\ue49f", "\ue4a0", "\ue4a1",
                    "\ue4a2", "\ue4a3", "\ue4a4",
                    "\ue4a5", "\ue4a6", "\ue4a7",
                    "\ue4a8"
            }
    };

    private final String[][] fadeDigits = {

            // Row 0
            {
                    "\ue4ae", "\ue4af", "\ue4b0",
                    "\ue4b1", "\ue4b2", "\ue4b3",
                    "\ue4b4", "\ue4b5", "\ue4b6",
                    "\ue4b7"
            },

            // Row 1
            {
                    "\ue4b8", "\ue4b9", "\ue4ba",
                    "\ue4bb", "\ue4bc", "\ue4bd",
                    "\ue4be", "\ue4bf", "\ue4c0",
                    "\ue4c1"
            },

            // Row 2
            {
                    "\ue4c2", "\ue4c3", "\ue4c4",
                    "\ue4c5", "\ue4c6", "\ue4c7",
                    "\ue4c8", "\ue4c9", "\ue4ca",
                    "\ue4cb"
            },

            // Row 3
            {
                    "\ue4cc", "\ue4cd", "\ue4ce",
                    "\ue4cf", "\ue4d0", "\ue4d1",
                    "\ue4d2", "\ue4d3", "\ue4d4",
                    "\ue4d5"
            },

            // Row 3
            {
                    "\ue4d6", "\ue4d7", "\ue4d8",
                    "\ue4d9", "\ue4da", "\ue4db",
                    "\ue4dc", "\ue4dd", "\ue4de",
                    "\ue4df"
            }
    };

    private final String[] settleCrits = {"\ue472","\ue473","\ue474","\ue475","\ue476"};

    private final String[] popCrits = {"\ue4a9","\ue4aa","\ue4ab","\ue4ac","\ue4ad"};

    private final String[] fadeCrits = {"\ue4e0","\ue4e1","\ue4e2","\ue4e3","\ue4e4"};

    private final int[] settleDigitWidth = {
            4,4,4,4,4,
            4,4,4,4,4
    };

    private final int[] popDigitWidth = {
            5,5,5,5,5,
            5,5,5,5,5
    };

    private final int settleCritWidth = 5;

    private final int popCritWidth = 7;

    public String build(DamageHud hud) {

        StringBuilder builder = new StringBuilder();

        long now = System.currentTimeMillis();

        for(int row = 0; row < ROWS; row++) {

            if(row != 0){
                int rowWidth = ROWS * SLOT_WIDTH;
                builder.append(buildNegativeSpace(rowWidth));
            }

            for(int col = 0; col < COLUMNS; col++) {

                int slotId = layout[row][col];

                if(slotId == -1) {
                    builder.append(EMPTY);
                    continue;
                }

                DamageSlot slot = hud.getSlot(slotId);

                if(slot != null && slot.isActive(now)) {
                    builder.append(renderEntry(slot.getEntry(), row));
                } else {
                    builder.append(EMPTY);
                }
            }
        }

        return builder.toString();
    }


    private String buildNegativeSpace(int pixels) {

        StringBuilder sb = new StringBuilder();

        while(pixels > 0) {

            if(pixels >= 4) {
                sb.append(NEG_4PX);
                pixels -= 4;
            }
            else if(pixels >= 2) {
                sb.append(NEG_2PX);
                pixels -= 2;
            }
            else {
                sb.append(NEG_1PX);
                pixels -= 1;
            }
        }

        return sb.toString();
    }

    private int computeEntryWidth(DamageEntry entry) {

        String damage = String.valueOf(entry.getAmount());

        int width = 0;

        long now = System.currentTimeMillis();
        switch (entry.getStage(now)){
            case SETTLE,FADE -> {
                for(char c : damage.toCharArray()) {
                    width += settleDigitWidth[c - '0'];
                }
            }
            case POP -> {
                for(char c : damage.toCharArray()) {
                    width += popDigitWidth[c - '0'];
                }
            }
        }



        return width;
    }


    private String renderEntry(DamageEntry entry, int row) {

        StringBuilder builder = new StringBuilder();

        long now = System.currentTimeMillis();

        DamageEntry.AnimationStage stage = entry.getStage(now);

        boolean crit = entry.isCrit();

        String damage = String.valueOf(entry.getAmount());

        String[][] digits;
        String[] critGlyph;

        int contentWidth = computeEntryWidth(entry);

        switch (stage){
            case POP -> {
                digits = popDigits;
                critGlyph = popCrits;

                if(crit){
                    contentWidth+= popCritWidth;
                }
            }
            case FADE -> {
                digits = fadeDigits;
                critGlyph = fadeCrits;

                if(crit){
                    contentWidth+= settleCritWidth;
                }
            }
            default -> {
                digits = settleDigits;
                critGlyph = settleCrits;

                if(crit){
                    contentWidth+= settleCritWidth;
                }
            }
        }



        int remaining = SLOT_WIDTH - contentWidth;

        int leftPad = remaining / 2;
        int rightPad = remaining - leftPad;

        // LEFT PAD
        builder.append(buildPositiveSpace(leftPad));

        if(crit){
            builder.append(critGlyph[row]);
        }

        // CONTENT
        for(char c : damage.toCharArray()) {

            int digit = c - '0';

            builder.append(digits[row][digit]);
        }

        // RIGHT PAD
        builder.append(buildPositiveSpace(rightPad));

        return builder.toString();
    }

    private String buildPositiveSpace(int pixels) {

        StringBuilder sb = new StringBuilder();

        while(pixels > 0) {

            if(pixels >= 4) {
                sb.append(POS_4PX);
                pixels -= 4;
            }
            else if(pixels >= 2) {
                sb.append(POS_2PX);
                pixels -= 2;
            }
            else {
                sb.append(POS_1PX);
                pixels -= 1;
            }
        }

        return sb.toString();
    }



}
