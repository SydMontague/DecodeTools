package net.digimonworld.decodetools.data.digimon;

import static net.digimonworld.decodetools.data.DataUtils.castOptional;
import static net.digimonworld.decodetools.data.DataUtils.convertKCAPtoList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.digimonworld.decodetools.data.DataUtils;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.kcap.HSMPKCAP;
import net.digimonworld.decodetools.res.kcap.NormalKCAP;
import net.digimonworld.decodetools.res.kcap.TDTMKCAP;
import net.digimonworld.decodetools.res.payload.GMIOPayload;
import net.digimonworld.decodetools.res.payload.VoidPayload;

public class PartnerDigimon {
    private HSMPKCAP model;
    private TDTMKCAP anim1;
    private Optional<TDTMKCAP> anim2;
    private Optional<TDTMKCAP> anim3;
    private Optional<TDTMKCAP> anim4;
    private Optional<TDTMKCAP> anim5;
    private Optional<TDTMKCAP> anim6;
    private Optional<TDTMKCAP> anim7;
    private Optional<TDTMKCAP> anim8;
    private Optional<TDTMKCAP> anim9;
    private Optional<TDTMKCAP> anim10;
    private Optional<TDTMKCAP> anim11;
    private Optional<TDTMKCAP> anim12;
    private Optional<TDTMKCAP> anim13;
    private Optional<TDTMKCAP> anim14;
    private List<AccessoryData> accessoryData;
    private Optional<NormalKCAP> data2;
    private Optional<NormalKCAP> data3;
    private Optional<GMIOPayload> grayDotSprite;
    
    public PartnerDigimon(AbstractKCAP kcap) {
        this.model = (HSMPKCAP) kcap.get(0);
        this.anim1 = (TDTMKCAP) kcap.get(1);
        this.anim2 = castOptional(kcap.get(2), TDTMKCAP.class);
        this.anim3 = castOptional(kcap.get(3), TDTMKCAP.class);
        this.anim4 = castOptional(kcap.get(4), TDTMKCAP.class);
        this.anim5 = castOptional(kcap.get(5), TDTMKCAP.class);
        this.anim6 = castOptional(kcap.get(6), TDTMKCAP.class);
        this.anim7 = castOptional(kcap.get(7), TDTMKCAP.class);
        this.anim8 = castOptional(kcap.get(8), TDTMKCAP.class);
        this.anim9 = castOptional(kcap.get(9), TDTMKCAP.class);
        this.anim10 = castOptional(kcap.get(10), TDTMKCAP.class);
        this.anim11 = castOptional(kcap.get(11), TDTMKCAP.class);
        this.anim12 = castOptional(kcap.get(12), TDTMKCAP.class);
        this.anim13 = castOptional(kcap.get(13), TDTMKCAP.class);
        this.anim14 = castOptional(kcap.get(14), TDTMKCAP.class);
        this.accessoryData = castOptional(kcap.get(15), NormalKCAP.class).map(a -> convertKCAPtoList(a, AccessoryData::new)).orElse(Collections.emptyList());
        this.data2 = castOptional(kcap.get(16), NormalKCAP.class);
        this.data3 = castOptional(kcap.get(17), NormalKCAP.class);
        this.grayDotSprite = castOptional(kcap.get(18), GMIOPayload.class);
    }
    
    public AbstractKCAP toKCAP() {
        List<ResPayload> entries = new ArrayList<>();
        
        entries.add(model);
        entries.add(anim1);
        anim2.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim3.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim4.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim5.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim6.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim7.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim8.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim9.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim10.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim11.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim12.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim13.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        anim14.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        entries.add(DataUtils.convertListToKCAP(accessoryData, false, false));
        data2.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        data3.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        grayDotSprite.ifPresentOrElse(entries::add, () -> entries.add(new VoidPayload(null)));
        
        return new NormalKCAP(null, entries, true, true);
    }
    
    public HSMPKCAP getModel() {
        return model;
    }

    public void setModel(HSMPKCAP model) {
        this.model = model;
    }

    public TDTMKCAP getAnim1() {
        return anim1;
    }

    public void setAnim1(TDTMKCAP anim1) {
        this.anim1 = anim1;
    }

    public Optional<TDTMKCAP> getAnim2() {
        return anim2;
    }

    public void setAnim2(Optional<TDTMKCAP> anim2) {
        this.anim2 = anim2;
    }

    public Optional<TDTMKCAP> getAnim3() {
        return anim3;
    }

    public void setAnim3(Optional<TDTMKCAP> anim3) {
        this.anim3 = anim3;
    }

    public Optional<TDTMKCAP> getAnim4() {
        return anim4;
    }

    public void setAnim4(Optional<TDTMKCAP> anim4) {
        this.anim4 = anim4;
    }

    public Optional<TDTMKCAP> getAnim5() {
        return anim5;
    }

    public void setAnim5(Optional<TDTMKCAP> anim5) {
        this.anim5 = anim5;
    }

    public Optional<TDTMKCAP> getAnim6() {
        return anim6;
    }

    public void setAnim6(Optional<TDTMKCAP> anim6) {
        this.anim6 = anim6;
    }

    public Optional<TDTMKCAP> getAnim7() {
        return anim7;
    }

    public void setAnim7(Optional<TDTMKCAP> anim7) {
        this.anim7 = anim7;
    }

    public Optional<TDTMKCAP> getAnim8() {
        return anim8;
    }

    public void setAnim8(Optional<TDTMKCAP> anim8) {
        this.anim8 = anim8;
    }

    public Optional<TDTMKCAP> getAnim9() {
        return anim9;
    }

    public void setAnim9(Optional<TDTMKCAP> anim9) {
        this.anim9 = anim9;
    }

    public Optional<TDTMKCAP> getAnim10() {
        return anim10;
    }

    public void setAnim10(Optional<TDTMKCAP> anim10) {
        this.anim10 = anim10;
    }

    public Optional<TDTMKCAP> getAnim11() {
        return anim11;
    }

    public void setAnim11(Optional<TDTMKCAP> anim11) {
        this.anim11 = anim11;
    }

    public Optional<TDTMKCAP> getAnim12() {
        return anim12;
    }

    public void setAnim12(Optional<TDTMKCAP> anim12) {
        this.anim12 = anim12;
    }

    public Optional<TDTMKCAP> getAnim13() {
        return anim13;
    }

    public void setAnim13(Optional<TDTMKCAP> anim13) {
        this.anim13 = anim13;
    }

    public Optional<TDTMKCAP> getAnim14() {
        return anim14;
    }

    public void setAnim14(Optional<TDTMKCAP> anim14) {
        this.anim14 = anim14;
    }

    public List<AccessoryData> getAccessoryData() {
        return accessoryData;
    }

    public void setAccessoryData(List<AccessoryData> accessoryData) {
        this.accessoryData = accessoryData;
    }

    public Optional<NormalKCAP> getData2() {
        return data2;
    }

    public void setData2(Optional<NormalKCAP> data2) {
        this.data2 = data2;
    }

    public Optional<NormalKCAP> getData3() {
        return data3;
    }

    public void setData3(Optional<NormalKCAP> data3) {
        this.data3 = data3;
    }

    public Optional<GMIOPayload> getGrayDotSprite() {
        return grayDotSprite;
    }

    public void setGrayDotSprite(Optional<GMIOPayload> grayDotSprite) {
        this.grayDotSprite = grayDotSprite;
    }
}
