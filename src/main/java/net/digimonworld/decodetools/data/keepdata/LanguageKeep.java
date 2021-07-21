package net.digimonworld.decodetools.data.keepdata;

import java.util.ArrayList;
import java.util.List;

import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.kcap.NormalKCAP;
import net.digimonworld.decodetools.res.payload.BTXPayload;

public class LanguageKeep {
    private final BTXPayload itemNames;
    private final BTXPayload itemDescription;
    private final BTXPayload itemDescription2;
    private final BTXPayload keyItemNames;
    private final BTXPayload keyItemDescription;
    private final BTXPayload accessoryNames;
    private final BTXPayload accessoryDescription;
    private final BTXPayload skillNames;
    private final BTXPayload skillDescriptions;
    private final BTXPayload finisherNames;
    private final BTXPayload finisherDescriptions;
    private final BTXPayload digimonNames;
    private final BTXPayload digimonDescription;
    private final BTXPayload characterNames;
    private final BTXPayload variousStrings1;
    private final BTXPayload variousStrings2;
    private final BTXPayload natureNames;
    private final BTXPayload medalNames;
    private final BTXPayload medalDescriptions;
    private final BTXPayload mailSender;
    private final BTXPayload mailSubject;
    private final BTXPayload mailContent;
    private final BTXPayload variousStrings3;
    private final BTXPayload newsTitle;
    private final BTXPayload newsContent;
    private final BTXPayload glossaryNames;
    private final BTXPayload glossaryDescriptions;
    private final BTXPayload cardNames1;
    private final BTXPayload cardNames2;
    private final BTXPayload cardDescriptions;
    private final BTXPayload cardSetNames;
    private final BTXPayload cardSetDescriptions;
    private final BTXPayload qrCodes;
    private final BTXPayload digitterMessages1;
    private final BTXPayload digitterMessages2;
    private final BTXPayload collosseumTitles;
    private final BTXPayload collosseumDescription;
    private final BTXPayload collosseumExtra;
    private final BTXPayload collosseumWonComment;
    private final BTXPayload collosseumLostComment;
    private final BTXPayload collosseumStartMessage;
    private final BTXPayload collosseumWonMessage;
    private final BTXPayload collosseumLostMessage;
    
    public LanguageKeep(AbstractKCAP kcap) {
        NormalKCAP kcap2 = (NormalKCAP) kcap.get(0);
        this.itemNames = (BTXPayload) kcap2.get(0);
        this.itemDescription = (BTXPayload) kcap2.get(1);
        this.itemDescription2 = (BTXPayload) kcap2.get(2);
        this.keyItemNames = (BTXPayload) kcap2.get(3);
        this.keyItemDescription = (BTXPayload) kcap2.get(4);
        this.accessoryNames = (BTXPayload) kcap2.get(5);
        this.accessoryDescription = (BTXPayload) kcap2.get(6);
        this.skillNames = (BTXPayload) kcap2.get(7);
        this.skillDescriptions = (BTXPayload) kcap2.get(8);
        this.finisherNames = (BTXPayload) kcap2.get(9);
        this.finisherDescriptions = (BTXPayload) kcap2.get(10);
        this.digimonNames = (BTXPayload) kcap2.get(11);
        this.digimonDescription = (BTXPayload) kcap2.get(12);
        this.characterNames = (BTXPayload) kcap2.get(13);
        this.variousStrings1 = (BTXPayload) kcap2.get(14);
        this.variousStrings2 = (BTXPayload) kcap2.get(15);
        this.natureNames = (BTXPayload) kcap2.get(16);
        this.medalNames = (BTXPayload) kcap2.get(17);
        this.medalDescriptions = (BTXPayload) kcap2.get(18);
        this.mailSender = (BTXPayload) kcap2.get(19);
        this.mailSubject = (BTXPayload) kcap2.get(20);
        this.mailContent = (BTXPayload) kcap2.get(21);
        this.variousStrings3 = (BTXPayload) kcap2.get(22);
        this.newsTitle = (BTXPayload) kcap2.get(23);
        this.newsContent = (BTXPayload) kcap2.get(24);
        this.glossaryNames = (BTXPayload) kcap2.get(25);
        this.glossaryDescriptions = (BTXPayload) kcap2.get(26);
        this.cardNames1 = (BTXPayload) kcap2.get(27);
        this.cardNames2 = (BTXPayload) kcap2.get(28);
        this.cardDescriptions = (BTXPayload) kcap2.get(29);
        this.cardSetNames = (BTXPayload) kcap2.get(30);
        this.cardSetDescriptions = (BTXPayload) kcap2.get(31);
        this.qrCodes = (BTXPayload) kcap2.get(32);
        this.digitterMessages1 = (BTXPayload) kcap2.get(33);
        this.digitterMessages2 = (BTXPayload) kcap2.get(34);
        this.collosseumTitles = (BTXPayload) kcap2.get(35);
        this.collosseumDescription = (BTXPayload) kcap2.get(36);
        this.collosseumExtra = (BTXPayload) kcap2.get(37);
        this.collosseumWonComment = (BTXPayload) kcap2.get(38);
        this.collosseumLostComment = (BTXPayload) kcap2.get(39);
        this.collosseumStartMessage = (BTXPayload) kcap2.get(40);
        this.collosseumWonMessage = (BTXPayload) kcap2.get(41);
        this.collosseumLostMessage = (BTXPayload) kcap2.get(42);
    }
    
    public AbstractKCAP toKCAP() {
        List<ResPayload> entries = new ArrayList<>();
        
        entries.add(itemNames);
        entries.add(itemDescription);
        entries.add(itemDescription2);
        entries.add(keyItemNames);
        entries.add(keyItemDescription);
        entries.add(accessoryNames);
        entries.add(accessoryDescription);
        entries.add(skillNames);
        entries.add(skillDescriptions);
        entries.add(finisherNames);
        entries.add(finisherDescriptions);
        entries.add(digimonNames);
        entries.add(digimonDescription);
        entries.add(characterNames);
        entries.add(variousStrings1);
        entries.add(variousStrings2);
        entries.add(natureNames);
        entries.add(medalNames);
        entries.add(medalDescriptions);
        entries.add(mailSender);
        entries.add(mailSubject);
        entries.add(mailContent);
        entries.add(variousStrings3);
        entries.add(newsTitle);
        entries.add(newsContent);
        entries.add(glossaryNames);
        entries.add(glossaryDescriptions);
        entries.add(cardNames1);
        entries.add(cardNames2);
        entries.add(cardDescriptions);
        entries.add(cardSetNames);
        entries.add(cardSetDescriptions);
        entries.add(qrCodes);
        entries.add(digitterMessages1);
        entries.add(digitterMessages2);
        entries.add(collosseumTitles);
        entries.add(collosseumDescription);
        entries.add(collosseumExtra);
        entries.add(collosseumWonComment);
        entries.add(collosseumLostComment);
        entries.add(collosseumStartMessage);
        entries.add(collosseumWonMessage);
        entries.add(collosseumLostMessage);
        
        NormalKCAP content = new NormalKCAP(null, entries, false, true);
        return new NormalKCAP(null, List.of(content), true, true);
    }

    public BTXPayload getItemNames() {
        return itemNames;
    }

    public BTXPayload getItemDescription() {
        return itemDescription;
    }

    public BTXPayload getItemDescription2() {
        return itemDescription2;
    }

    public BTXPayload getKeyItemNames() {
        return keyItemNames;
    }

    public BTXPayload getKeyItemDescription() {
        return keyItemDescription;
    }

    public BTXPayload getAccessoryNames() {
        return accessoryNames;
    }

    public BTXPayload getAccessoryDescription() {
        return accessoryDescription;
    }

    public BTXPayload getSkillNames() {
        return skillNames;
    }

    public BTXPayload getSkillDescriptions() {
        return skillDescriptions;
    }

    public BTXPayload getFinisherNames() {
        return finisherNames;
    }

    public BTXPayload getFinisherDescriptions() {
        return finisherDescriptions;
    }

    public BTXPayload getDigimonNames() {
        return digimonNames;
    }

    public BTXPayload getDigimonDescription() {
        return digimonDescription;
    }

    public BTXPayload getCharacterNames() {
        return characterNames;
    }

    public BTXPayload getVariousStrings1() {
        return variousStrings1;
    }

    public BTXPayload getVariousStrings2() {
        return variousStrings2;
    }

    public BTXPayload getNatureNames() {
        return natureNames;
    }

    public BTXPayload getMedalNames() {
        return medalNames;
    }

    public BTXPayload getMedalDescriptions() {
        return medalDescriptions;
    }

    public BTXPayload getMailSender() {
        return mailSender;
    }

    public BTXPayload getMailSubject() {
        return mailSubject;
    }

    public BTXPayload getMailContent() {
        return mailContent;
    }

    public BTXPayload getVariousStrings3() {
        return variousStrings3;
    }

    public BTXPayload getNewsTitle() {
        return newsTitle;
    }

    public BTXPayload getNewsContent() {
        return newsContent;
    }

    public BTXPayload getGlossaryNames() {
        return glossaryNames;
    }

    public BTXPayload getGlossaryDescriptions() {
        return glossaryDescriptions;
    }

    public BTXPayload getCardNames1() {
        return cardNames1;
    }

    public BTXPayload getCardNames2() {
        return cardNames2;
    }

    public BTXPayload getCardDescriptions() {
        return cardDescriptions;
    }

    public BTXPayload getCardSetNames() {
        return cardSetNames;
    }

    public BTXPayload getCardSetDescriptions() {
        return cardSetDescriptions;
    }

    public BTXPayload getQrCodes() {
        return qrCodes;
    }

    public BTXPayload getDigitterMessages1() {
        return digitterMessages1;
    }

    public BTXPayload getDigitterMessages2() {
        return digitterMessages2;
    }

    public BTXPayload getCollosseumTitles() {
        return collosseumTitles;
    }

    public BTXPayload getCollosseumDescription() {
        return collosseumDescription;
    }

    public BTXPayload getCollosseumExtra() {
        return collosseumExtra;
    }

    public BTXPayload getCollosseumWonComment() {
        return collosseumWonComment;
    }

    public BTXPayload getCollosseumLostComment() {
        return collosseumLostComment;
    }

    public BTXPayload getCollosseumStartMessage() {
        return collosseumStartMessage;
    }

    public BTXPayload getCollosseumWonMessage() {
        return collosseumWonMessage;
    }

    public BTXPayload getCollosseumLostMessage() {
        return collosseumLostMessage;
    }
}
