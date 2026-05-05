package me.angeloo.mystica.Utility.TextRenderer;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class AsciiFontAtlas {

    private final Map<Character, PixelMatrix> glyphs = new HashMap<>();

    public AsciiFontAtlas(){

        glyphs.put('0', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10011000,
                (byte)0b10101000,
                (byte)0b11001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('1', new PixelMatrix(new byte[]{
                (byte)0b00100000,
                (byte)0b01100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b11111000,
                (byte)0b00000000,
        }));

        glyphs.put('2', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b00001000,
                (byte)0b00110000,
                (byte)0b01000000,
                (byte)0b10001000,
                (byte)0b11111000,
                (byte)0b00000000,
        }));

        glyphs.put('3', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b00001000,
                (byte)0b00110000,
                (byte)0b00001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('4', new PixelMatrix(new byte[]{
                (byte)0b00011000,
                (byte)0b00101000,
                (byte)0b01001000,
                (byte)0b10001000,
                (byte)0b11111000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b00000000,
        }));

        glyphs.put('5', new PixelMatrix(new byte[]{
                (byte)0b11111000,
                (byte)0b10000000,
                (byte)0b11110000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('6', new PixelMatrix(new byte[]{
                (byte)0b00110000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('7', new PixelMatrix(new byte[]{
                (byte)0b11111000,
                (byte)0b10001000,
                (byte)0b00001000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('8', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('9', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00001000,
                (byte)0b00010000,
                (byte)0b01100000,
                (byte)0b00000000,
        }));

        glyphs.put('a', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01110000,
                (byte)0b00001000,
                (byte)0b01111000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00000000,
        }));

        glyphs.put('`', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('&', new PixelMatrix(new byte[]{
                (byte)0b00100000,
                (byte)0b01010000,
                (byte)0b00100000,
                (byte)0b01101000,
                (byte)0b10110000,
                (byte)0b10010000,
                (byte)0b01101000,
                (byte)0b00000000,
        }));

        glyphs.put('*', new PixelMatrix(new byte[]{
                (byte)0b10100000,
                (byte)0b01000000,
                (byte)0b10100000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('@', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b01111000,
                (byte)0b10000100,
                (byte)0b10110100,
                (byte)0b10100100,
                (byte)0b10111100,
                (byte)0b10000000,
                (byte)0b01111000,
        }));

        glyphs.put('b', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10110000,
                (byte)0b11001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b00000000,
        }));

        glyphs.put('|', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('\\', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00010000,
                (byte)0b00010000,
                (byte)0b00001000,
                (byte)0b00000000,
        }));

        glyphs.put('c', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10000000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('A', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b11111000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('^', new PixelMatrix(new byte[]{
                (byte)0b00100000,
                (byte)0b01010000,
                (byte)0b10001000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('B', new PixelMatrix(new byte[]{
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b00000000,
        }));

        glyphs.put('C', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('D', new PixelMatrix(new byte[]{
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b00000000,
        }));

        glyphs.put('E', new PixelMatrix(new byte[]{
                (byte)0b11111000,
                (byte)0b10000000,
                (byte)0b11100000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b11111000,
                (byte)0b00000000,
        }));

        glyphs.put('F', new PixelMatrix(new byte[]{
                (byte)0b11111000,
                (byte)0b10000000,
                (byte)0b11100000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('G', new PixelMatrix(new byte[]{
                (byte)0b01111000,
                (byte)0b10000000,
                (byte)0b10011000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('H', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b11111000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('I', new PixelMatrix(new byte[]{
                (byte)0b11100000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b11100000,
                (byte)0b00000000,
        }));

        glyphs.put('J', new PixelMatrix(new byte[]{
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('K', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b10010000,
                (byte)0b11100000,
                (byte)0b10010000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('L', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b11111000,
                (byte)0b00000000,
        }));

        glyphs.put('M', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b11011000,
                (byte)0b10101000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('N', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b11001000,
                (byte)0b10101000,
                (byte)0b10011000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('O', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put(':', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put(',', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b10000000,
        }));

        glyphs.put('P', new PixelMatrix(new byte[]{
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('Q', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10010000,
                (byte)0b01101000,
                (byte)0b00000000,
        }));

        glyphs.put('R', new PixelMatrix(new byte[]{
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('S', new PixelMatrix(new byte[]{
                (byte)0b01111000,
                (byte)0b10000000,
                (byte)0b01110000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('T', new PixelMatrix(new byte[]{
                (byte)0b11111000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('U', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('{', new PixelMatrix(new byte[]{
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('}', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('V', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01010000,
                (byte)0b01010000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('W', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10101000,
                (byte)0b11011000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('X', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b01010000,
                (byte)0b00100000,
                (byte)0b01010000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('Y', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b01010000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('Z', new PixelMatrix(new byte[]{
                (byte)0b11111000,
                (byte)0b00001000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b11111000,
                (byte)0b00000000,
        }));

        glyphs.put('d', new PixelMatrix(new byte[]{
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b01101000,
                (byte)0b10011000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00000000,
        }));

        glyphs.put('-', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11111000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('$', new PixelMatrix(new byte[]{
                (byte)0b00100000,
                (byte)0b01111000,
                (byte)0b10000000,
                (byte)0b01110000,
                (byte)0b00001000,
                (byte)0b11110000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('e', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b11111000,
                (byte)0b10000000,
                (byte)0b01111000,
                (byte)0b00000000,
        }));

        glyphs.put('=', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11111000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11111000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('!', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('f', new PixelMatrix(new byte[]{
                (byte)0b00110000,
                (byte)0b01000000,
                (byte)0b11110000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b00000000,
        }));

        glyphs.put('g', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01111000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00001000,
                (byte)0b11110000,
        }));

        glyphs.put('h', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10110000,
                (byte)0b11001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('#', new PixelMatrix(new byte[]{
                (byte)0b01010000,
                (byte)0b01010000,
                (byte)0b11111000,
                (byte)0b01010000,
                (byte)0b11111000,
                (byte)0b01010000,
                (byte)0b01010000,
                (byte)0b00000000,
        }));

        glyphs.put('i', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('j', new PixelMatrix(new byte[]{
                (byte)0b00001000,
                (byte)0b00000000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b00001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
        }));

        glyphs.put('k', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10010000,
                (byte)0b10100000,
                (byte)0b11000000,
                (byte)0b10100000,
                (byte)0b10010000,
                (byte)0b00000000,
        }));

        glyphs.put('l', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b00000000,
        }));

        glyphs.put('[', new PixelMatrix(new byte[]{
                (byte)0b11100000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b11100000,
                (byte)0b00000000,
        }));

        glyphs.put('<', new PixelMatrix(new byte[]{
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00010000,
                (byte)0b00000000,
        }));

        glyphs.put('m', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11010000,
                (byte)0b10101000,
                (byte)0b10101000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('>', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('n', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('o', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01110000,
                (byte)0b00000000,
        }));

        glyphs.put('p', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10110000,
                (byte)0b11001000,
                (byte)0b10001000,
                (byte)0b11110000,
                (byte)0b10000000,
                (byte)0b10000000,
        }));

        glyphs.put('(', new PixelMatrix(new byte[]{
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put(')', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('%', new PixelMatrix(new byte[]{
                (byte)0b10001000,
                (byte)0b10010000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b01001000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('.', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('+', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b11111000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('q', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01101000,
                (byte)0b10011000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00001000,
                (byte)0b00001000,
        }));

        glyphs.put('?', new PixelMatrix(new byte[]{
                (byte)0b01110000,
                (byte)0b10001000,
                (byte)0b00001000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b00000000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('"', new PixelMatrix(new byte[]{
                (byte)0b10100000,
                (byte)0b10100000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('r', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10110000,
                (byte)0b11001000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put(']', new PixelMatrix(new byte[]{
                (byte)0b11100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b00100000,
                (byte)0b11100000,
                (byte)0b00000000,
        }));

        glyphs.put('s', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b01111000,
                (byte)0b10000000,
                (byte)0b01110000,
                (byte)0b00001000,
                (byte)0b11110000,
                (byte)0b00000000,
        }));

        glyphs.put(';', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10000000,
                (byte)0b10000000,
        }));

        glyphs.put('/', new PixelMatrix(new byte[]{
                (byte)0b00001000,
                (byte)0b00010000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b10000000,
                (byte)0b00000000,
        }));

        glyphs.put('\'', new PixelMatrix(new byte[]{
                (byte)0b10000000,
                (byte)0b10000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('t', new PixelMatrix(new byte[]{
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b11100000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b01000000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('~', new PixelMatrix(new byte[]{
                (byte)0b01100100,
                (byte)0b10011000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
        }));

        glyphs.put('u', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00000000,
        }));

        glyphs.put('_', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11111000,
        }));

        glyphs.put('v', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01010000,
                (byte)0b00100000,
                (byte)0b00000000,
        }));

        glyphs.put('w', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10101000,
                (byte)0b10101000,
                (byte)0b01111000,
                (byte)0b00000000,
        }));

        glyphs.put('x', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10001000,
                (byte)0b01010000,
                (byte)0b00100000,
                (byte)0b01010000,
                (byte)0b10001000,
                (byte)0b00000000,
        }));

        glyphs.put('y', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b10001000,
                (byte)0b01111000,
                (byte)0b00001000,
                (byte)0b11110000,
        }));

        glyphs.put('z', new PixelMatrix(new byte[]{
                (byte)0b00000000,
                (byte)0b00000000,
                (byte)0b11111000,
                (byte)0b00010000,
                (byte)0b00100000,
                (byte)0b01000000,
                (byte)0b11111000,
                (byte)0b00000000,
        }));


        //Bukkit.getLogger().info(glyphs.keySet().size() + " pixel matrices registered");
    }


    public PixelMatrix get(char c){
        return glyphs.get(c);
    }

    public Map<Character, PixelMatrix> getGlyphs() {
        return glyphs;
    }
}
