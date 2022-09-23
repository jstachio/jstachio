package com.snaphop.staticmustache.apt;

import java.util.ArrayList;
import java.util.List;

public class CodeNewLineSplitter {
    
    public static List<String> split(String s , String delim) {
        int dl = delim.length();
        int sl = s.length();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < sl; ) {
            int end = s.indexOf(delim, i);
            end = end < 0 ? sl : Integer.min(end + dl, sl);
            String chunk = s.substring(i, end);
            tokens.add(chunk);
            i = end;
        }
        return tokens;
    }

}
