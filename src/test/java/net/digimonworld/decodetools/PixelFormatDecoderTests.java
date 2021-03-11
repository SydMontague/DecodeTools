package net.digimonworld.decodetools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.digimonworld.decodetools.core.Utils;

public class PixelFormatDecoderTests {
    @Test
    public void testExpand4To8() {
        assertEquals(0b11111111, Utils.extend4To8(0b1111));
        assertEquals(0b00010001, Utils.extend4To8(0b0001));
        assertEquals(0b11111111, Utils.extend4To8(0b111111111111));
        assertEquals(0b11111111, Utils.extend4To8(-1));
        assertEquals(0b00000000, Utils.extend4To8(0b0000));
    }
    
    @Test
    public void testExpand5To8() {
        assertEquals(0b11111111, Utils.extend5To8(0b11111));
        assertEquals(0b00001000, Utils.extend5To8(0b00001));
        assertEquals(0b10001100, Utils.extend5To8(0b10001));
        assertEquals(0b11111111, Utils.extend5To8(0b111111111111));
        assertEquals(0b11111111, Utils.extend5To8(-1));
        assertEquals(0b00000000, Utils.extend5To8(0b00000));
    }
    
    @Test
    public void testExpand6To8() {
        assertEquals(0b11111111, Utils.extend6To8(0b111111));
        assertEquals(0b00000100, Utils.extend6To8(0b000001));
        assertEquals(0b10000110, Utils.extend6To8(0b100001));
        assertEquals(0b11111111, Utils.extend6To8(0b111111111111));
        assertEquals(0b11111111, Utils.extend6To8(-1));
        assertEquals(0b00000000, Utils.extend6To8(0b000000));
    }
}
