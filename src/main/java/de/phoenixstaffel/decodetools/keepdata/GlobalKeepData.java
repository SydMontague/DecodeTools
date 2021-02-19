package de.phoenixstaffel.decodetools.keepdata;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.phoenixstaffel.decodetools.core.StreamAccess;
import de.phoenixstaffel.decodetools.res.ResPayload;
import de.phoenixstaffel.decodetools.res.kcap.AbstractKCAP;
import de.phoenixstaffel.decodetools.res.kcap.NormalKCAP;
import de.phoenixstaffel.decodetools.res.payload.GenericPayload;
import de.phoenixstaffel.decodetools.res.payload.VoidPayload;

public class GlobalKeepData {
    private final List<Digimon> digimonData;
    private final List<DigimonRaising> raiseData;
    private final GenericPayload levelParam;
    private final GenericPayload wakeupTimes;
    private final List<EvoRequirement> evoRequirements;
    private final GenericPayload items;
    private final GenericPayload unk1;
    private final List<Skill> skills;
    private final List<Finisher> finisher;
    private final GenericPayload enemyData;
    private final GenericPayload unk3;
    private final GenericPayload shops;
    private final GenericPayload unk5;
    private final GenericPayload unk6;
    private final GenericPayload enemyMapData;
    private final AbstractKCAP unk7;
    private final GenericPayload fish;
    private final GenericPayload fishSpots;
    private final GenericPayload dishes;
    private final GenericPayload menu;
    private final GenericPayload fishBait;
    private final GenericPayload unk11;
    private final GenericPayload cards;
    private final GenericPayload cardSets;
    private final GenericPayload cardTrades;
    private final AbstractKCAP unk12;
    private final GenericPayload treasureParam;
    private final GenericPayload treasureLoot;
    private final AbstractKCAP arena;
    private final VoidPayload void0;
    private final VoidPayload void1;
    private final VoidPayload void2;
    private final GenericPayload deviShop;
    private final GenericPayload unk14;
    private final GenericPayload unk15;
    private final GenericPayload unk16;
    private final GenericPayload medalRank;
    private final GenericPayload unk17;
    private final GenericPayload unk18;
    private final GenericPayload unk19;
    private final GenericPayload unk20;
    private final GenericPayload unk21;
    private final GenericPayload storageCards;
    private final AbstractKCAP unk22;
    private final GenericPayload unk23;
    private final GenericPayload unk24;
    private final AbstractKCAP unk25;
    private final GenericPayload typeEffectiveness;
    private final GenericPayload unk27;
    private final GenericPayload unk28;
    private final GenericPayload unk29;
    private final GenericPayload unk30;
    private final GenericPayload decodeLevel;
    private final GenericPayload unk31;
    
    private static <T> List<T> convertKCAPtoList(AbstractKCAP input, Function<StreamAccess, T> generator) {
        return input.getEntries().stream().map(a -> new StreamAccess(((GenericPayload) a).getData())).map(generator).collect(Collectors.toList());
    }
    
    private static NormalKCAP convertListToKCAP(List<? extends KeepData> data, boolean genericAligned, boolean isUnknownFlagSet) {
        return new NormalKCAP(null, data.stream().map(KeepData::toPayload).collect(Collectors.toList()), genericAligned, isUnknownFlagSet);
    }
    
    public GlobalKeepData(AbstractKCAP kcap) {
        this.digimonData = convertKCAPtoList((AbstractKCAP) kcap.get(0), Digimon::new);
        this.raiseData = convertKCAPtoList((AbstractKCAP) kcap.get(1), DigimonRaising::new);
        this.levelParam = (GenericPayload) kcap.get(2);
        this.wakeupTimes = (GenericPayload) kcap.get(3);
        this.evoRequirements = convertKCAPtoList((AbstractKCAP) kcap.get(4), EvoRequirement::new);
        this.items = (GenericPayload) kcap.get(5);
        this.unk1 = (GenericPayload) kcap.get(6);
        this.skills = convertKCAPtoList((AbstractKCAP) kcap.get(7), Skill::new);
        this.finisher = convertKCAPtoList((AbstractKCAP) kcap.get(8), Finisher::new);
        this.enemyData = (GenericPayload) kcap.get(9);
        this.unk3 = (GenericPayload) kcap.get(10);
        this.shops = (GenericPayload) kcap.get(11);
        this.unk5 = (GenericPayload) kcap.get(12);
        this.unk6 = (GenericPayload) kcap.get(13);
        this.enemyMapData = (GenericPayload) kcap.get(14);
        this.unk7 = (AbstractKCAP) kcap.get(15);
        this.fish = (GenericPayload) kcap.get(16);
        this.fishSpots = (GenericPayload) kcap.get(17);
        this.dishes = (GenericPayload) kcap.get(18);
        this.menu = (GenericPayload) kcap.get(19);
        this.fishBait = (GenericPayload) kcap.get(20);
        this.unk11 = (GenericPayload) kcap.get(21);
        this.cards = (GenericPayload) kcap.get(22);
        this.cardSets = (GenericPayload) kcap.get(23);
        this.cardTrades = (GenericPayload) kcap.get(24);
        this.unk12 = (AbstractKCAP) kcap.get(25);
        this.treasureParam = (GenericPayload) kcap.get(26);
        this.treasureLoot = (GenericPayload) kcap.get(27);
        this.arena = (AbstractKCAP) kcap.get(28);
        this.void0 = (VoidPayload) kcap.get(29);
        this.void1 = (VoidPayload) kcap.get(30);
        this.void2 = (VoidPayload) kcap.get(31);
        this.deviShop = (GenericPayload) kcap.get(32);
        this.unk14 = (GenericPayload) kcap.get(33);
        this.unk15 = (GenericPayload) kcap.get(34);
        this.unk16 = (GenericPayload) kcap.get(35);
        this.medalRank = (GenericPayload) kcap.get(36);
        this.unk17 = (GenericPayload) kcap.get(37);
        this.unk18 = (GenericPayload) kcap.get(38);
        this.unk19 = (GenericPayload) kcap.get(39);
        this.unk20 = (GenericPayload) kcap.get(40);
        this.unk21 = (GenericPayload) kcap.get(41);
        this.storageCards = (GenericPayload) kcap.get(42);
        this.unk22 = (AbstractKCAP) kcap.get(43);
        this.unk23 = (GenericPayload) kcap.get(44);
        this.unk24 = (GenericPayload) kcap.get(45);
        this.unk25 = (AbstractKCAP) kcap.get(46);
        this.typeEffectiveness = (GenericPayload) kcap.get(47);
        this.unk27 = (GenericPayload) kcap.get(48);
        this.unk28 = (GenericPayload) kcap.get(49);
        this.unk29 = (GenericPayload) kcap.get(50);
        this.unk30 = (GenericPayload) kcap.get(51);
        this.decodeLevel = (GenericPayload) kcap.get(52);
        this.unk31 = (GenericPayload) kcap.get(53);
    }
    
    public AbstractKCAP toKCAP() {
        List<ResPayload> entries = new ArrayList<>();

        entries.add(convertListToKCAP(digimonData, false, false));
        entries.add(convertListToKCAP(raiseData, false, false));
        entries.add(levelParam);
        entries.add(wakeupTimes);
        entries.add(convertListToKCAP(evoRequirements, true, false));
        entries.add(items);
        entries.add(unk1);
        entries.add(convertListToKCAP(skills, true, false));
        entries.add(convertListToKCAP(finisher, true, false));
        entries.add(enemyData);
        entries.add(unk3);
        entries.add(shops);
        entries.add(unk5);
        entries.add(unk6);
        entries.add(enemyMapData);
        entries.add(unk7); // true
        entries.add(fish);
        entries.add(fishSpots);
        entries.add(dishes);
        entries.add(menu);
        entries.add(fishBait);
        entries.add(unk11);
        entries.add(cards);
        entries.add(cardSets);
        entries.add(cardTrades);
        entries.add(unk12); // true
        entries.add(treasureParam);
        entries.add(treasureLoot);
        entries.add(arena); // true
        entries.add(void0);
        entries.add(void1);
        entries.add(void2);
        entries.add(deviShop);
        entries.add(unk14);
        entries.add(unk15);
        entries.add(unk16);
        entries.add(medalRank);
        entries.add(unk17);
        entries.add(unk18);
        entries.add(unk19);
        entries.add(unk20);
        entries.add(unk21);
        entries.add(storageCards);
        entries.add(unk22); // true
        entries.add(unk23);
        entries.add(unk24);
        entries.add(unk25); // true
        entries.add(typeEffectiveness);
        entries.add(unk27);
        entries.add(unk28);
        entries.add(unk29);
        entries.add(unk30);
        entries.add(decodeLevel);
        entries.add(unk31);
        
        return new NormalKCAP(null, entries, true, true);
    }

    public List<Digimon> getDigimonData() {
        return digimonData;
    }

    public List<DigimonRaising> getRaiseData() {
        return raiseData;
    }

    public List<EvoRequirement> getEvoRequirements() {
        return evoRequirements;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<Finisher> getFinisher() {
        return finisher;
    }
}
