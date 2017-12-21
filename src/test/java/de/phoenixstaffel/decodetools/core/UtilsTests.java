package de.phoenixstaffel.decodetools.core;

import static de.phoenixstaffel.decodetools.TestUtils.assertException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTests {
    
    @Test
    public void testAlignment() {
        assertEquals(1, Utils.align(1, 1));
        assertEquals(2, Utils.align(1, 2));
        assertEquals(2, Utils.align(2, 2));
        assertEquals(9, Utils.align(1, 9));
        assertEquals(3612, Utils.align(3611, 2));
        assertEquals(0, Utils.align(-1, 2));
        
        assertException(IllegalArgumentException.class, () -> Utils.align(Integer.MAX_VALUE, 2));
        assertException(IllegalArgumentException.class, () -> Utils.align(1, 0));
        assertException(IllegalArgumentException.class, () -> Utils.align(1, -1));
        
        assertEquals(1L, Utils.align(1L, 1));
        assertEquals(2L, Utils.align(1L, 2));
        assertEquals(9L, Utils.align(1L, 9));
        assertEquals(36120000000000L, Utils.align(36119999999999L, 2));
        assertEquals(0L, Utils.align(-1L, 2));
        
        assertException(IllegalArgumentException.class, () -> Utils.align(Long.MAX_VALUE, 2));
        assertException(IllegalArgumentException.class, () -> Utils.align(1L, 0));
        assertException(IllegalArgumentException.class, () -> Utils.align(1L, -1));
    }
    
    @Test
    public void testIsPow2() {
        assertTrue(Utils.isPowOf2(2));
        assertTrue(Utils.isPowOf2(4));
        assertTrue(Utils.isPowOf2(4096));
        assertFalse(Utils.isPowOf2(4095));
        assertFalse(Utils.isPowOf2(3));
        assertFalse(Utils.isPowOf2(0x80000000));
        assertTrue(Utils.isPowOf2(0x40000000));
    }
    
    @Test
    public void testCrop() {
        assertEquals(10, Utils.crop(10, 5, 15));
        assertEquals(-10, Utils.crop(-10, 5, -15));
        assertEquals(5, Utils.crop(1, 5, 15));
        assertEquals(15, Utils.crop(100, 5, 15));
    }
    
    @Test
    public void testAdd3BitSigned() {
        assertEquals(0, Utils.add3BitSigned(0,0));
        assertEquals(3, Utils.add3BitSigned(0,3));
        assertEquals(-1, Utils.add3BitSigned(0,7));
        assertEquals(-3, Utils.add3BitSigned(1,4));
        assertEquals(10017, Utils.add3BitSigned(10021,4));
        assertEquals(10024, Utils.add3BitSigned(10021,3));
        
        assertException(IllegalArgumentException.class, () -> Utils.add3BitSigned(100, -1));
        assertException(IllegalArgumentException.class, () -> Utils.add3BitSigned(100, 10));
        assertException(IllegalArgumentException.class, () -> Utils.add3BitSigned(100, 100000));
    }
    
    @Test
    public void testGetBitValue() {
        assertTrue(Utils.getBitValue(1, 0));
        assertTrue(Utils.getBitValue(7, 0));
        assertTrue(Utils.getBitValue(7, 1));
        assertFalse(Utils.getBitValue(5, 1));
        assertTrue(Utils.getBitValue(-1, 63));
        assertTrue(Utils.getBitValue(-1, 5));
        assertFalse(Utils.getBitValue(5, 60));
        assertFalse(Utils.getBitValue(5, 30));
    }
    
    /*
     * public static BufferedImage flipImage(BufferedImage image) {
     * public static BufferedImage flipImage(BufferedImage image, boolean newImage) {
     * public static BufferedImage flipImageVertically(BufferedImage image) {
     * public static List<File> fileOrder(File file) {
     * public static long getSubInteger(long value, int bit, int length) {
     * public static long extend4To8(long value) {
     * public static long extend5To8(long value) {
     * public static long extend6To8(long value) {
     * public static int[] untile(short width, short height, int[] pixelData) {
     * public static int[] tile(int width, int height, int[] pixelData) {
     * private static int getMortonOffset(int x, int y) {
     * private static int mortonInterleave(int x, int y) {
     */
}
