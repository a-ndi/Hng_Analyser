package com.hng.Analyser.Service.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NaturalLanguageParser {
    public static class ParseResult {
        public Map<String,Object> parsedFilters = new HashMap<>();
    }

    /**
     * Very small heuristic parser to support example queries:
     * - "all single word palindromic strings" -> word_count=1, is_palindrome=true
     * - "strings longer than 10 characters" -> min_length=11
     * - "strings containing the letter z" -> contains_character=z
     * - "palindromic strings that contain the first vowel" -> is_palindrome=true, contains_character=a (heuristic)
     */
    public static ParseResult parse(String query) {
        if (query == null || query.trim().isEmpty()) throw new IllegalArgumentException("empty query");
        String q = query.toLowerCase();

        ParseResult res = new ParseResult();

        if (q.contains("palind") || q.contains("palindrom")) {
            res.parsedFilters.put("is_palindrome", true);
        }
        if (q.contains("single word") || q.contains("one word") || q.contains("single-word")) {
            res.parsedFilters.put("word_count", 1);
        }

        // longer than N or longer than X characters
        Pattern longer = Pattern.compile("longer than (\\d+)"); // e.g. longer than 10
        Matcher m = longer.matcher(q);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            res.parsedFilters.put("min_length", n + 1);
        }

        // "strings longer than 10 characters" (explicit)
        Pattern longerChar = Pattern.compile("longer than (\\d+) (characters|chars)");
        Matcher m2 = longerChar.matcher(q);
        if (m2.find()) {
            int n = Integer.parseInt(m2.group(1));
            res.parsedFilters.put("min_length", n + 1);
        }

        // contains letter <x>
        Pattern contains = Pattern.compile("contain(?:ing|s)? (?:the )?letter (\\w)");
        Matcher m3 = contains.matcher(q);
        if (m3.find()) {
            res.parsedFilters.put("contains_character", m3.group(1));
        }

        // "strings containing the letter z"
        Pattern contains2 = Pattern.compile("containing the letter (\\w)");
        Matcher m4 = contains2.matcher(q);
        if (m4.find()) {
            res.parsedFilters.put("contains_character", m4.group(1));
        }

        // "first vowel" heuristic -> 'a'
        if (q.contains("first vowel")) {
            res.parsedFilters.put("contains_character", "a");
        }

        return res;
    }
}

