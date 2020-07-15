package de.phoenixstaffel.decodetools.gui.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.phoenixstaffel.decodetools.Main;

public class LinebreakUtil {
    private static final int X_VARIABLE_LENGTH = 8;
    private static final Map<Integer, Integer> NAME_MAP = new HashMap<>();
    
    private LinebreakUtil() {
    }
    
    static {
        try {
            BufferedReader a = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("names.csv")));
            
            String str;
            while ((str = a.readLine()) != null) {
                String[] arr = str.split(",");
                NAME_MAP.put(Integer.parseInt(arr[0]), arr[1].length());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String calculateLinebreaks(String in, int charLimit) {
        StringBuilder b = new StringBuilder();
        String[] arr = in.split("<p>");
        
        for (int i = 0; i < arr.length; i++) {
            b.append(calculateLinebreaksPage(arr[i], charLimit));
            if (i + 1 < arr.length)
                b.append("<p>\n");
        }
        
        return b.toString();
    }
    
    private static String calculateLinebreaksPage(String in, int charLimit) {
        String input = in.replaceAll("\s+", " ");
        
        int length = calculateStringLength(input);
        int localLimit = length > charLimit ? (int) Math.ceil(length / 2D) : charLimit;
        int lineNum = 0;
        
        StringTokenizer token = new StringTokenizer(input);
        StringBuilder output = new StringBuilder();
        
        int count = 0;
        while (token.hasMoreTokens()) {
            String s = token.nextToken();
            int localLength = calculateStringLength(s);
            
            if (lineNum == 0 && count + localLength + 1 > localLimit) {
                output.append("\n");
                lineNum++;
                count = 0;
            }
            else if (count != 0) {
                output.append(" ");
                count += 1;
            }
            
            output.append(s);
            count += localLength;
        }
        
        return output.toString();
    }
    
    private static int calculateStringLength(String str) {
        int length = 0;
        
        StringCharacterIterator itr = new StringCharacterIterator(str);
        char c = itr.first();
        
        do {
            if (c == '<') {
                char next = itr.next();
                StringBuilder id = new StringBuilder();
                
                while (itr.next() != '>')
                    id.append(itr.current());
                
                switch (next) {
                    case 'p': // page break
                    case 'w': // delay
                        break;
                    case 'x': // variable
                        length += X_VARIABLE_LENGTH;
                        break;
                    case 'n': // name
                        length += getNameLength(id.toString());
                        break;
                    default:
                        break;
                }
            }
            length += 1;
        } while (CharacterIterator.DONE != (c = itr.next()));
        
        return length;
    }
    
    private static int getNameLength(String str) {
        return NAME_MAP.getOrDefault(Integer.parseInt(str), X_VARIABLE_LENGTH);
    }
}
