package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

public class PixelMatrix {

    private final char c;
    private final byte[] rows; //8 bits per row
    private final int width;

    public PixelMatrix(char c, byte[] rows){
        this.c = c;
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

            //was +1, changed to +2 because adding a space at the end of each
            int width = lastFilled + 2;

            if (width > max) {
                max = width;
            }
        }

        //this is because of the space character
        if(max==0){
            return 3;
        }

        return max;
    }

    public char getChar(){
        return c;
    }

}
