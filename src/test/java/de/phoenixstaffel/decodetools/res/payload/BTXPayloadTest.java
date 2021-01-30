package de.phoenixstaffel.decodetools.res.payload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Test;

import de.phoenixstaffel.decodetools.core.StreamAccess;
import de.phoenixstaffel.decodetools.core.Tuple;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.payload.BTXPayload.BTXEntry;

public class BTXPayloadTest {
    
    @Test
    public void testSpeakers() throws IOException {
        String[] compare = new String[] {
                "おめはつえぇね\nやっぱり都会は色んなやつがおるなー",
                "おめと戦えて満足したっぺ\nオラァ、もっとつよぐなるため旅にでるだよ",
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
            ResPayload f = ResPayload.craft(access);
            BTXPayload btx = (BTXPayload) f;
            
            List<Tuple<Integer, BTXEntry>> list = btx.getEntries();
            
            assertEquals(compare.length, list.size()); // check size
            
            for (int i = 0; i < list.size(); i++) {
                assertEquals(compare[i], list.get(i).getValue().getString());
                if(hasMeta) {
                    assertTrue(list.get(i).getValue().getMeta().isPresent());
                    assertEquals(217, list.get(i).getValue().getMeta().get().getSpeaker());
                }
                else
                    assertTrue(list.get(i).getValue().getMeta().isEmpty());
            }
        }
    }
}
