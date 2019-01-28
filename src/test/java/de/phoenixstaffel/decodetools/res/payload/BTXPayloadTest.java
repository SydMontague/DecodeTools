package de.phoenixstaffel.decodetools.res.payload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.core.StreamAccess;
import de.phoenixstaffel.decodetools.core.Tuple;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload.BTXEntry;

public class BTXPayloadTest {
    
    @Test
    public void testSpeakers() throws IOException {
        String[] compare = new String[] {
                "おめはつえぇね\nやっぱり都会<r2_とかい>は色<r1_いろ>んなやつがおるなー",
                "おめと戦<r1_たたか>えて満足<r2_まんぞく>したっぺ\nオラァ、もっとつよぐなるため旅<r1_たび>にでるだよ",
                "まだの〜！"                
        };
        
        try (InputStream in = BTXPayloadTest.class.getResourceAsStream("/btxSpeakers.res")) {
            testBTX(in, compare, "Speakers", true);
        }
    }
    
    @Test
    public void testNoSpeakers() throws IOException {
        String[] compare = new String[] {
                "ごうかい",
                "ゆうかん",
                "がんばりや",
                "ひょうひょう",
                "かしこい",
                "れいせい",
                "がまんづよい",
                "せんりゃくか",
                "おくびょう",
                "コンビA",
                "コンビB",
                "策士",
                "バースト",
                "ビルド",
                "狡猾",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
        };
        try (InputStream in = BTXPayloadTest.class.getResourceAsStream("/btxNoSpeakers.res")) {
            testBTX(in, compare, "NoSpeakers", false);
        }
    }
    
    private void testBTX(InputStream in, String[] compare, String suffix, boolean hasMeta) throws IOException {
        byte[] arr = new byte[in.available()];
        in.read(arr);
        
        ByteBuffer buff = ByteBuffer.wrap(arr);
        
        try (StreamAccess access = new StreamAccess(buff)) {
            ResFile f = new ResFile(access);
            BTXPayload btx = (BTXPayload) f.getRoot();
            
            List<Tuple<Integer, BTXEntry>> list = btx.getEntries();
            
            assertEquals(compare.length, list.size()); // check size
            
            for (int i = 0; i < list.size(); i++) {
                assertEquals(compare[i], list.get(i).getValue().getString());
                if(hasMeta) {
                    assertNotEquals(null, list.get(i).getValue().getMeta());
                    assertEquals(217, list.get(i).getValue().getMeta().getSpeaker());
                }
                else
                    assertEquals(null, list.get(i).getValue().getMeta());
            }
            
            File outputFile = File.createTempFile("output", suffix);
            try(FileAccess target = new FileAccess(outputFile)) {
                btx.writeKCAP(target, null);
            }
            
            byte[] output = Files.readAllBytes(outputFile.toPath());
            
            assertTrue(Arrays.equals(arr, output));
            outputFile.delete();
            
            list.stream().map(a -> a.getValue()).forEach(a -> a.setString(a.getString()));
            
            outputFile = File.createTempFile("output", suffix);
            try(FileAccess target = new FileAccess(outputFile)) {
                btx.writeKCAP(target, null);
            }
            
            assertTrue(Arrays.equals(arr, output));
            outputFile.delete();
        }
    }
    
    @Test
    public void testLanguageKeep() throws IOException {
        try (InputStream in = BTXPayloadTest.class.getResourceAsStream("/LanguageKeep_jp.res")) {
            byte[] arr = new byte[in.available()];
            in.read(arr);
            
            ByteBuffer buff = ByteBuffer.wrap(arr);
            
            try (StreamAccess access = new StreamAccess(buff)) {
                ResFile f = new ResFile(access);
                
                File outputFile = File.createTempFile("output", "Speakers");
                f.repack(outputFile);
                
                byte[] output = Files.readAllBytes(outputFile.toPath());
                
                assertTrue(Arrays.equals(arr, output));
                outputFile.delete();
            }
        }
    }
}
