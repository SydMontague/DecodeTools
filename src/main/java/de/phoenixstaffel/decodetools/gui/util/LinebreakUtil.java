package de.phoenixstaffel.decodetools.gui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import de.phoenixstaffel.decodetools.Main;
import de.phoenixstaffel.decodetools.core.FileAccess;
import de.phoenixstaffel.decodetools.res.ResFile;
import de.phoenixstaffel.decodetools.res.ResPayload.Payload;
import de.phoenixstaffel.decodetools.res.payload.TNFOPayload;

public class LinebreakUtil {
    private static final String X_VARIABLE_PLACEHOLDER = "AAAAAAAA";
    private static final Map<Integer, String> NAME_STRING_MAP = new HashMap<>();
    
    private LinebreakUtil() {
    }
    
    static {
        try {
            BufferedReader a = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("names.csv")));
            
            String str;
            while ((str = a.readLine()) != null) {
                String[] arr = str.split(",");
                NAME_STRING_MAP.put(Integer.parseInt(arr[0]), arr[1]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String calculateLinebreaks(String in, double fontSize, double maxWidth, TNFOPayload font, boolean balance) {
        StringBuilder b = new StringBuilder();
        String[] arr = in.split("<p>");

        for (int i = 0; i < arr.length; i++) {
            String input = arr[i].replaceAll("\\s+", " ").trim();
            double width = calculateStringWidth(input, font, fontSize);
            
            if(width < maxWidth) // single line
                b.append(input);
            else if(width > maxWidth * 1.5D && balance) { // multi line, for Digitter
                StringTokenizer token = new StringTokenizer(input);
                double currentWidth = 0D;
                int lineCount = 1;
                
                while(token.hasMoreTokens()) {
                    String s = token.nextToken();
                    double lWidth = calculateStringWidth(s, font, fontSize);
                    currentWidth += lWidth;

                    if(currentWidth > maxWidth) {
                        b.append('\n');
                        lineCount++;
                        currentWidth = lWidth;
                    }

                    currentWidth += font.getSpaceWidth() * (fontSize / font.getReferenceSize());
                    b.append(s);
                    b.append(" ");
                }
                
                if(lineCount > 3)
                    Main.LOGGER.warning(() -> String.format("Linecount is over 3. If applied to Digitter it will break: %s", input));
            }
            else { // double line
                if(width > maxWidth * 2)
                    Main.LOGGER.warning(() -> String.format("String is too long for a textbox: %s", input));
                
                double currentWidth = 0D;
                
                List<StringToken> list = new ArrayList<>();
                StringTokenizer token = new StringTokenizer(input);
                
                while(token.hasMoreTokens()) {
                    String s = token.nextToken();
                    SplitClass split = SplitClass.WORD;

                    currentWidth += calculateStringWidth(s, font, fontSize);
                    
                    if(isEndOfSentence(s))
                        split = SplitClass.SENTENCE;
                    else if(isEndOfSubSentence(s) && currentWidth / maxWidth > 0.4D)
                        split = SplitClass.SUBSENTENCE;
                    
                    if(currentWidth > maxWidth || width - currentWidth > maxWidth)
                        split = SplitClass.NONE;
                    
                    list.add(new StringToken(s, Math.abs((width / 2) - currentWidth), split));
                    currentWidth += font.getSpaceWidth() * (fontSize / font.getReferenceSize());
                }
                
                int bestIndex = 0;
                StringToken bestToken = list.get(0);

                for (int j = 1; j < list.size(); j++) {
                    StringToken currentToken = list.get(j);
                    
                    if(currentToken.isBetterSplitThan(bestToken)) {
                        bestIndex = j;
                        bestToken = currentToken;
                    }
                }

                StringBuilder sb = new StringBuilder();
                String line1 = list.subList(0, bestIndex + 1).stream().map(StringToken::getString).collect(Collectors.joining(" "));
                String line2 = list.subList(bestIndex + 1, list.size()).stream().map(StringToken::getString).collect(Collectors.joining(" "));
                
                double lineWidth1 = calculateStringWidth(line1, font, fontSize);
                double lineWidth2 = calculateStringWidth(line2, font, fontSize);
                
                if(lineWidth1 > maxWidth) {
                    Main.LOGGER.warning(() -> String.format("String is too long for a textbox: %s", input));
                    Main.LOGGER.warning(() -> String.format("Width: %#.2f | Line: %s", lineWidth1, line1));
                    
                }
                if(lineWidth2 > maxWidth) {
                    Main.LOGGER.warning(() -> String.format("String is too long for a textbox: %s", input));
                    Main.LOGGER.warning(() -> String.format("Width: %#.2f | Line: %s", lineWidth2, line2));
                }
                
                sb.append(line1);
                sb.append("\n");
                sb.append(line2);
                b.append(sb.toString());
            }

            if (i + 1 < arr.length)
                b.append("<p>\n");
        }
        
        return b.toString().trim();
    }
    
    private static boolean isEndOfSentence(String s) {
        return s.endsWith("!") || s.endsWith(".") || s.endsWith("?");
    }
    
    private static boolean isEndOfSubSentence(String s) {
        return s.endsWith(",") || s.endsWith("-") || s.endsWith(";") || s.endsWith(":");
    }
    
    private static class StringToken {
        String string;
        SplitClass split;
        double balanceValue;
        
        public StringToken(String string, double balanceValue, SplitClass split) {
            this.string = string;
            this.split = split;
            this.balanceValue = balanceValue;
        }
        
        public boolean isBetterSplitThan(StringToken otherToken) {
            if(split.ordinal() < otherToken.split.ordinal())
                return true;

            if(split.ordinal() > otherToken.split.ordinal())
                return false;
            
            return balanceValue < otherToken.balanceValue;
        }
        
        public String getString() {
            return string;
        }
    }
    
    private enum SplitClass {
        SENTENCE,
        SUBSENTENCE,
        WORD,
        NONE;
    }
    
    public static double calculateStringWidth(String string, TNFOPayload font, double fontSize) {
        double width = 0;

        StringCharacterIterator itr = new StringCharacterIterator(string);
        char c = itr.first();

        do {
            if(c == '<') 
                width += calculateStringWidth(parseToken(itr), font, fontSize);
            else if(c == ' ')
                width += font.getSpaceWidth() * (fontSize / font.getReferenceSize());
            else
                width += Byte.toUnsignedInt(font.getEntry(c).getTextWidth()) * (fontSize / font.getReferenceSize());
        } while (CharacterIterator.DONE != (c = itr.next()));
        
        return width;
    }
    
    private static String parseToken(StringCharacterIterator itr) {
        char next = itr.next();
        StringBuilder id = new StringBuilder();
        
        while (itr.next() != '>') {
            if (itr.current() == CharacterIterator.DONE)
                throw new IllegalStateException("String contains unterminated placeholder!");
            id.append(itr.current());
        }
        
        switch (next) {
            case 'x': // variable
                return X_VARIABLE_PLACEHOLDER;
            case 'n': // name
                return NAME_STRING_MAP.getOrDefault(Integer.parseInt(id.toString()), X_VARIABLE_PLACEHOLDER);
            case 'p': // page break
            case 'w': // delay
            default:
                return "";
        }
    }
}
