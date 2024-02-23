package com.convert.tinyurl.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class Base62Converter {
    private static final String BASE62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE62_LENGTH = BASE62_CHARACTERS.length();
    private static final Map<Character, Integer> CHAR_TO_INDEX_MAP = new HashMap<>();

    private Base62Converter() {
    }
    //this is the optimized code generated with the help of chatgpt...
    static {
        for (int i = 0; i < BASE62_LENGTH; i++) {
            CHAR_TO_INDEX_MAP.put(BASE62_CHARACTERS.charAt(i), i);
        }
    }

    public static String encode(int number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.insert(0, BASE62_CHARACTERS.charAt(number % BASE62_LENGTH));
            number /= BASE62_LENGTH;
        }
        return sb.toString();
    }

    public static int decode(String base62String) {
        int number = 0;
        for (int i = 0; i < base62String.length(); i++) {
            number = number * BASE62_LENGTH + CHAR_TO_INDEX_MAP.get(base62String.charAt(i));
        }
        return number;
    }

}
