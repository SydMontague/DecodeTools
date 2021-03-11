package net.digimonworld.decodetools.core;

import static net.digimonworld.decodetools.TestUtils.assertException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

import net.digimonworld.decodetools.core.Utils;

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
        assertEquals(0, Utils.add3BitSigned(0, 0));
        assertEquals(3, Utils.add3BitSigned(0, 3));
        assertEquals(-1, Utils.add3BitSigned(0, 7));
        assertEquals(-3, Utils.add3BitSigned(1, 4));
        assertEquals(10017, Utils.add3BitSigned(10021, 4));
        assertEquals(10024, Utils.add3BitSigned(10021, 3));
        
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

        assertException(IllegalArgumentException.class, () -> Utils.getBitValue(-1, -1));
        assertException(IllegalArgumentException.class, () -> Utils.getBitValue(-1, 65));
    }
    
    @Test
    public void testGetSubInteger() {
        assertEquals(0xF, Utils.getSubInteger(0xFF, 0, 4));
        assertEquals(0x7, Utils.getSubInteger(0xFF, 0, 3));
        assertEquals(0xF, Utils.getSubInteger(0xFF, 2, 4));
        assertEquals(-1, Utils.getSubInteger(-1, 0, 64));
        assertEquals(Long.MAX_VALUE, Utils.getSubInteger(-1, 0, 63));
        assertEquals(4362, Utils.getSubInteger(0x54122154, 5, 13));
        
        assertException(IllegalArgumentException.class, () -> Utils.getSubInteger(541564, 66, 0));
        assertException(IllegalArgumentException.class, () -> Utils.getSubInteger(541564, -1, 0));
        assertException(IllegalArgumentException.class, () -> Utils.getSubInteger(541564, 30, -1));
        assertException(IllegalArgumentException.class, () -> Utils.getSubInteger(541564, 30, 0));
        assertException(IllegalArgumentException.class, () -> Utils.getSubInteger(541564, 30, 44));
    }
    
    @Test
    public void testExtend4to8() {
        assertEquals(0x11, Utils.extend4To8(0x01));
        assertEquals(0x11, Utils.extend4To8(0x11));
        assertEquals(0xFF, Utils.extend4To8(0x0F));
        assertEquals(0xFF, Utils.extend4To8(-1));
        assertEquals(0xEE, Utils.extend4To8(-2));
    }
    
    @Test
    public void testExtend5to8() {
        assertEquals(0x08, Utils.extend5To8(0x01));
        assertEquals(0x21, Utils.extend5To8(0x04));
        assertEquals(0xFF, Utils.extend5To8(0x1F));
        assertEquals(0xE7, Utils.extend5To8(0x1C));
        assertEquals(0xFF, Utils.extend5To8(-1));
        assertEquals(0xF7, Utils.extend5To8(-2));
    }
    
    @Test
    public void testExtend6to8() {
        assertEquals(0b00000100, Utils.extend6To8(0x01));
        assertEquals(0b00001000, Utils.extend6To8(0x02));
        assertEquals(0b11001111, Utils.extend6To8(0x33));
        assertEquals(0b11111111, Utils.extend6To8(-1));
        assertEquals(0b11111011, Utils.extend6To8(-2));
    }
    
    @Test
    public void testFlipImage() throws IOException {
        BufferedImage normal = ImageIO.read(UtilsTests.class.getResource("/unflipped.png"));
        BufferedImage hori = ImageIO.read(UtilsTests.class.getResource("/flippedHori.png"));
        BufferedImage verti = ImageIO.read(UtilsTests.class.getResource("/flippedVerti.png"));
        
        assertNotNull(normal);
        assertNotNull(hori);
        assertNotNull(verti);
        
        int[] horiData = hori.getRGB(0, 0, hori.getWidth(), hori.getHeight(), null, 0, hori.getWidth());
        int[] vertiData = verti.getRGB(0, 0, verti.getWidth(), verti.getHeight(), null, 0, verti.getWidth());
        int[] normalData = normal.getRGB(0, 0, normal.getWidth(), normal.getHeight(), null, 0, normal.getWidth());
        
        BufferedImage flippedHori = Utils.mirrorImageHorizontal(normal, true);
        int[] flippedHoriData = flippedHori.getRGB(0, 0, flippedHori.getWidth(), flippedHori.getHeight(), null, 0, flippedHori.getWidth());
        
        assertNotSame(flippedHori, normal);
        assertTrue(Arrays.equals(flippedHoriData, horiData));
        
        BufferedImage flippedVerti = Utils.mirrorImageVertical(normal, true);
        int[] flippedVertiData = flippedVerti.getRGB(0, 0, flippedVerti.getWidth(), flippedVerti.getHeight(), null, 0, flippedVerti.getWidth());
        
        assertNotSame(flippedVerti, normal);
        assertTrue(Arrays.equals(flippedVertiData, vertiData));
        
        BufferedImage b2 = Utils.mirrorImageHorizontal(normal);
        
        assertSame(b2, normal);
        
        Utils.mirrorImageHorizontal(b2);
        
        int[] doubleFlippedData = b2.getRGB(0, 0, b2.getWidth(), b2.getHeight(), null, 0, b2.getWidth());
        assertTrue(Arrays.equals(normalData, doubleFlippedData));
        
        assertException(IllegalArgumentException.class, () -> Utils.mirrorImageHorizontal(null));
        assertException(IllegalArgumentException.class, () -> Utils.mirrorImageVertical(null));
    }
    
    //FIXME the order is not guaranteed, check if it's necessary and act accordingly
    @Test
    public void testListFiles() throws IOException {
        File root = Files.createTempDirectory("listFiles").toFile(); //new File("./listFile");
        List<File> expected = Arrays.asList(new File(root, "test.test"),
                                            new File(root, "test1.test"),
                                            new File(root, "test2.test"),
                                            new File(root, "Test3.test"),
                                            new File(root, "bla/aest.test"),
                                            new File(root, "bla/best.test"),
                                            new File(root, "cla/cest.test"),
                                            new File(root, "cla/dest.test"));
        
        expected.forEach(a -> {
            try {
                a.getParentFile().mkdirs();
                a.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        List<File> files = Utils.listFiles(root);

        assertTrue(Utils.listFiles(null).isEmpty());
        assertEquals(expected, files);
        assertEquals(Arrays.asList(new File(root, "test.test")), Utils.listFiles(new File(root, "test.test")));
    }
    
    @Test
    public void testMortonInterleave() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = Utils.class.getDeclaredMethod("mortonInterleave", int.class, int.class);
        m.setAccessible(true);
        
        assertEquals(0b111111, m.invoke(null, 0b0111, 0b0111));
        assertEquals(0b111110, m.invoke(null, 0b1110, 0b0111));
        assertEquals(0b101010, m.invoke(null, 0b0000, 0b0111));
        assertEquals(0b010101, m.invoke(null, 0b0111, 0b0000));
        assertEquals(0b110011, m.invoke(null, 0b1101, 0b1101));
        assertEquals(0b111111, m.invoke(null, 0b11110111, 0b11110111));
        assertEquals(0b111111, m.invoke(null, 0b11110111, 0b11110111));
        assertEquals(0b000000, m.invoke(null, 0b0, 0b0));
        assertEquals(0b111111, m.invoke(null, 0xFFFFFFFF, 0xFFFFFFFF));
    }
    
    /*
     * TODO public static int[] untile(short width, short height, int[] pixelData) {
     * TODO public static int[] tile(int width, int height, int[] pixelData) {
     * TODO private static int getMortonOffset(int x, int y) {
     */
}
