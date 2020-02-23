package de.phoenixstaffel.decodetools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import org.junit.Test;

import de.phoenixstaffel.decodetools.core.Utils;

public class PixelFormatDecoderTests {
    
    private byte[] bla() {
        byte[] b = new byte[256*256*2];
        ByteBuffer buff = ByteBuffer.wrap(b);
        
        for(int x = 0; x < 256; x++)
            for(int y = 0; y < 256; y++)
            {
                buff.put((byte) x);
                buff.put((byte) y);
            }
        
        return buff.array();
    }
    
    @Test
    public void testLA8() throws IOException {
        BufferedImage i = ImageIO.read(new File("src/test/resources/LAtest.png"));
        Files.write(Paths.get("test.bin"), bla(), StandardOpenOption.CREATE);
        byte[] b = PixelFormatEncoder.convertToLA8(i);
        Files.write(Paths.get("test2.bin"), b, StandardOpenOption.CREATE);
        
        int[] rgb = Utils.untile((short) i.getWidth(), (short) i.getHeight(), PixelFormatDecoder.convertFromLA8(b, i.getWidth(), i.getHeight()));
        int[] rgb2 = i.getRGB(0, 0, i.getWidth(), i.getHeight(), null, 0, i.getWidth());
        
        BufferedImage i2 = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
        i2.setRGB(0, 0, i2.getWidth(), i2.getHeight(), rgb, 0, i2.getWidth());
        ImageIO.write(i2, "PNG", new File("LA8output.png"));
        
        assertArrayEquals(rgb, rgb2);
    }
    
    @Test
    public void testExpand4To8() {
        assertEquals(Utils.extend4To8(0b1111), 0b11111111);
        assertEquals(Utils.extend4To8(0b0001), 0b00010001);
        assertEquals(Utils.extend4To8(0b111111111111), 0b11111111);
        assertEquals(Utils.extend4To8(-1), 0b11111111);
        assertEquals(Utils.extend4To8(0b0000), 0b00000000);
    }
    
    @Test
    public void testExpand5To8() {
        assertEquals(Utils.extend5To8(0b11111), 0b11111111);
        assertEquals(Utils.extend5To8(0b00001), 0b00001000);
        assertEquals(Utils.extend5To8(0b10001), 0b10001100);
        assertEquals(Utils.extend5To8(0b111111111111), 0b11111111);
        assertEquals(Utils.extend5To8(-1), 0b11111111);
        assertEquals(Utils.extend5To8(0b00000), 0b00000000);
    }
    
    @Test
    public void testExpand6To8() {
        assertEquals(Utils.extend6To8(0b111111), 0b11111111);
        assertEquals(Utils.extend6To8(0b000001), 0b00000100);
        assertEquals(Utils.extend6To8(0b100001), 0b10000110);
        assertEquals(Utils.extend6To8(0b111111111111), 0b11111111);
        assertEquals(Utils.extend6To8(-1), 0b11111111);
        assertEquals(Utils.extend6To8(0b000000), 0b00000000);
    }
}
