package me.angeloo.mystica.Utility.TextRenderer;

public class PixelMatrix {

    private final byte[] rows; //8 bits per row

    public PixelMatrix(byte[] rows){
        this.rows = rows;
    }

    public boolean isFilled(int x, int y){
        return (rows[y] & (1 << (7 - x))) != 0;
    }



}
