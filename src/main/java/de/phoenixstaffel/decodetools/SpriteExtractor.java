package de.phoenixstaffel.decodetools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

public class SpriteExtractor {
    
    private SpriteExtractor() {
        //no implementation
    }
    
    // 0x22CC3580
    public static void main(String[] args) throws IOException {
        FileChannel chan = FileChannel.open(new File("texture.data").toPath());
        
        int i = 0;
        BufferedImage im = new BufferedImage(16 * 16, 16 * 12, BufferedImage.TYPE_INT_ARGB);
        
        while (chan.position() < chan.size()) {
            ByteBuffer buff = ByteBuffer.allocate(128 * 16 * 3);
            chan.read(buff);
            buff.flip();
            
            BufferedImage image = readImage(buff);
            
            ImageIO.write(image, "PNG", new File("Output\\Sprites\\" + i + ".png"));
            ImageIO.write(image.getSubimage(0, 0, 16, 16), "PNG", new File("Output\\Sprites\\16x16\\" + i + ".png"));
            
            int[] arr = image.getRaster().getPixels(0, 0, 16, 16, new int[16*16*4]);
            im.getRaster().setPixels((i % 16) * 16, (i / 16) * 16, 16, 16, arr);
            i++;
        }
        
        ImageIO.write(im, "PNG", new File("Output\\spritesheet.png"));
        chan.close();
    }
    
    private static BufferedImage readImage(ByteBuffer buff) {
        BufferedImage im = new BufferedImage(128, 16, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < 16; y++)
            for (int x = 0; x < 128; x++) {
                
                int red = Byte.toUnsignedInt(buff.get());
                int green = Byte.toUnsignedInt(buff.get());
                int blue = Byte.toUnsignedInt(buff.get());
                
                int alpha = ((blue & 0x08) >> 3) * 255;
                blue = ((blue & 0xF0) >> 1) + ((green & 0x04) << 5);
                green = green & 0xF8;
                
                im.setRGB(x, y, new Color(red, green, blue, alpha).getRGB());
            }
        
        return im;
    }
    
}
