package de.phoenixstaffel.decodetools;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class PixelFormatDecoderTests {
    
    @Test
    public void testExpand4To8() {
        assertEquals(PixelFormatDecoder.extend4To8(0b1111), 0b11111111);
        assertEquals(PixelFormatDecoder.extend4To8(0b0001), 0b00010001);
        assertEquals(PixelFormatDecoder.extend4To8(0b111111111111), 0b11111111);
        assertEquals(PixelFormatDecoder.extend4To8(-1), 0b11111111);
        assertEquals(PixelFormatDecoder.extend4To8(0b0000), 0b00000000);
    }
    
    @Test
    public void testExpand5To8() {
        assertEquals(PixelFormatDecoder.extend5To8(0b11111), 0b11111111);
        assertEquals(PixelFormatDecoder.extend5To8(0b00001), 0b00001000);
        assertEquals(PixelFormatDecoder.extend5To8(0b10001), 0b10001100);
        assertEquals(PixelFormatDecoder.extend5To8(0b111111111111), 0b11111111);
        assertEquals(PixelFormatDecoder.extend5To8(-1), 0b11111111);
        assertEquals(PixelFormatDecoder.extend5To8(0b00000), 0b00000000);
    }
    
    @Test
    public void testExpand6To8() {
        assertEquals(PixelFormatDecoder.extend6To8(0b111111), 0b11111111);
        assertEquals(PixelFormatDecoder.extend6To8(0b000001), 0b00000100);
        assertEquals(PixelFormatDecoder.extend6To8(0b100001), 0b10000110);
        assertEquals(PixelFormatDecoder.extend6To8(0b111111111111), 0b11111111);
        assertEquals(PixelFormatDecoder.extend6To8(-1), 0b11111111);
        assertEquals(PixelFormatDecoder.extend6To8(0b000000), 0b00000000);
    }
    
    @Test
    public void testGetSubInteger() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        Method m = PixelFormatDecoder.class.getDeclaredMethod("getSubInteger", long.class, int.class, int.class);
        m.setAccessible(true);
        
        assertEquals(m.invoke(null, 0b00001110, 1, 3), 0b111L);
        assertEquals(m.invoke(null, 0b00001110, 4, 3), 0b000L);
        assertEquals(m.invoke(null, 0b00001110, 0, 4), 0b1110L);
        
        try {
            m.invoke(null, 0b00001110, 62, 3);
            fail("No exception reading bits 62-64 (out of bounds).");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception reading bits 62-64 (out of bounds).");
        }
        
        try {
            m.invoke(null, 0b00001110, -1, 3);
            fail("No exception starting at bit -1.");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception starting at bit -1.");
        }
        
        try {
            m.invoke(null, 0b00001110, 62, 0);
            fail("No exception reading 0 bits.");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception reading 0 bits.");
        }
        
        try {
            m.invoke(null, 0b00001110, 62, -1);
            fail("No exception reading -1 bits.");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception reading -1 bits.");
        }
    }
    
    @Test
    public void testGetBitValue() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        Method m = PixelFormatDecoder.class.getDeclaredMethod("getBitValue", long.class, int.class);
        m.setAccessible(true);
        
        assertEquals(m.invoke(null, 0b00001110, 0), false);
        assertEquals(m.invoke(null, 0b00001110, 1), true);
        
        try {
            m.invoke(null, 0b00001110, 64);
            fail("No exception reading bit 64 (out of bounds).");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception reading bits 62-64 (out of bounds).");
        }
        
        try {
            m.invoke(null, 0b00001110, -1);
            fail("No exception reading bit -1 (out of bounds).");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception reading bit -1 (out of bounds).");
        }
    }
    
    @Test
    public void testAdd3BitSigned() throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        Method m = PixelFormatDecoder.class.getDeclaredMethod("add3BitSigned", long.class, long.class);
        m.setAccessible(true);
        
        assertEquals(m.invoke(null, 100L, 3L), 103L);
        assertEquals(m.invoke(null, 100L, 7L), 99L);
        
        try {
            m.invoke(null, 100L, -1L);
            fail("No exception with 2nd argument being smaller than 0.");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception with 2nd argument being smaller than 0.");
        }
        
        try {
            m.invoke(null, 100L, 8L);
            fail("No exception with 2nd argument being larger than 7.");
        } catch(InvocationTargetException e) {
            if(!(e.getTargetException() instanceof IllegalArgumentException))
                fail("No exception with 2nd argument being larger than 7.");
        }
    }
}
