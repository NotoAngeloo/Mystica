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

    private final String[][] digits = {

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

    private final String[] crits = {"\ue472","\ue473","\ue474","\ue475","\ue476"};

    private final int[] digitWidth = {
            4,4,4,4,4,
            4,4,4,4,4
    };

    private final int critWidth = 5;


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

    /*public String build(DamageHud hud) {

        StringBuilder builder = new StringBuilder();

        long now = System.currentTimeMillis();

        for(int row = 0; row < ROWS; row++) {

            if(row != 0){
                int rowWidth = ROWS * SLOT_WIDTH;
                builder.append(buildNegativeSpace(rowWidth));
            }


            for(int i = 0; i < hud.getSlots().length; i++) {

                if(i / COLUMNS != row) continue;

                DamageSlot slot = hud.getSlots()[i];

                if(slot.isActive(now)) {

                    builder.append(
                            renderEntry(slot.getEntry(), row)
                    );
                }
                else{
                    builder.append(EMPTY);
                }
            }
        }

        return builder.toString();
    }*/



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

        for(char c : damage.toCharArray()) {
            width += digitWidth[c - '0'];
        }

        return width;
    }

    private String renderEntry(DamageEntry entry, int row) {

        StringBuilder builder = new StringBuilder();

        boolean crit = entry.isCrit();

        String damage = String.valueOf(entry.getAmount());

        int contentWidth = computeEntryWidth(entry);

        if(crit){
            contentWidth+=critWidth;
        }

        int remaining = SLOT_WIDTH - contentWidth;

        int leftPad = remaining / 2;
        int rightPad = remaining - leftPad;

        // LEFT PAD
        builder.append(buildPositiveSpace(leftPad));

        if(crit){
            builder.append(crits[row]);
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
