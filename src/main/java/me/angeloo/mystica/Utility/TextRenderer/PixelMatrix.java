package me.angeloo.mystica.Utility.TextRenderer;

public class PixelMatrix {

    private final byte[] rows; //8 bits per row
    private final int width;

    public PixelMatrix(byte[] rows){
        this.rows = rows;
        this.width = computeWidth(rows);
    }

    public boolean isFilled(int x, int y){
        return (rows[y] & (1 << (7 - x))) != 0;
    }

    public int getWidth(){
        return width;
    }

    private int computeWidth(byte[] rows) {

        int max = 0;

        for (byte row : rows) {

            int value = row & 0xFF; // prevent sign issues

            if (value == 0) continue;

            // find rightmost 1 bit
            int lastFilled = 7 - Integer.numberOfTrailingZeros(value);

            int width = lastFilled + 1;

            if (width > max) {
                max = width;
            }
        }

        return max;
    }

}
