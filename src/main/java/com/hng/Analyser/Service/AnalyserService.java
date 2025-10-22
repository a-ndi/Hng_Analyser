package com.hng.Analyser.Service;


import com.hng.Analyser.Model.AnalysedString;
import com.hng.Analyser.Model.AnalysedStringProperties;
import com.hng.Analyser.Repo.AnalyserRepo;
import com.hng.Analyser.Service.errors.ResourceNotFoundException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnalyserService {

    private final AnalyserRepo analyserRepo;

    public AnalyserService(AnalyserRepo analyserRepo) {
        this.analyserRepo = analyserRepo;
    }


    public AnalysedString analyse(String inputString){
        AnalysedStringProperties properties  = new AnalysedStringProperties();
        properties.setLength(inputString.length());
        properties.set_palindrome(isPalindrome(inputString));
        properties.setWord_count(countWords(inputString));
        properties.setUnique_characters(uniqueChars(inputString));
        properties.setCharacter_frequency_map(frequencyMap(inputString));
        properties.setSha256_hash(shaHash(inputString));

        AnalysedString result = new AnalysedString();
        result.setProperties(properties);
        result.setCreated_at(String.valueOf(System.currentTimeMillis()));
        result.setValue(inputString);

        return analyserRepo.save(result);
    }

    public boolean deleteByValue(String value) {
        Optional<AnalysedString> found = analyserRepo.findByValue(value);
        if (found.isEmpty()) {
            return false; // or throw new ResourceNotFoundException(...)
        }
        analyserRepo.delete(found.get());
        return true;
    }

    public Optional<AnalysedString> getByValue(String value) {
        return analyserRepo.findByValue(value);
    }

    // Filter logic for GET /api/analyse
    public List<AnalysedString> filter(Boolean isPalindrome,
                                       Integer minLength,
                                       Integer maxLength,
                                       Integer wordCount,
                                       String containsCharacter) {
        List<AnalysedString> all = analyserRepo.findAll();

        return all.stream()
                .filter(s -> isPalindrome == null || s.getProperties().is_palindrome() == isPalindrome)
                .filter(s -> minLength == null || s.getProperties().getLength() >= minLength)
                .filter(s -> maxLength == null || s.getProperties().getLength() <= maxLength)
                .filter(s -> wordCount == null || s.getProperties().getWord_count() == wordCount)
                .filter(s -> containsCharacter == null || s.getValue().contains(containsCharacter))
                .collect(Collectors.toList());
    }

    @NonNull
    private boolean isPalindrome(String inputString){
        String reversedString = new StringBuilder(inputString).reverse().toString();
        return inputString.equalsIgnoreCase(reversedString);
    }

    private int countWords(String inputString){
        String[] words = inputString.split("\\s+");
        return words.length;

    }

    private String uniqueChars(String inputString){
        StringBuilder uniqueChars = new StringBuilder();
        for (int i = 0; i < inputString.length(); i++) {
            char c = inputString.charAt(i);
            if (c != ' ' && inputString.indexOf(c) == inputString.lastIndexOf(c)) {
                uniqueChars.append(c);
            }
        }
        return uniqueChars.toString();
    }

    private String shaHash(String inputString){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(inputString.getBytes());
            BigInteger number = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Pad with leading zeros to make it 64 chars (standard SHA-256 length)
            while (hexString.length() < 64) {
                hexString.insert(0, '0');
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Character, Integer> frequencyMap(String inputString){

        Map<Character, Integer> frequency = new HashMap<>();
        for (char c : inputString.toCharArray()) {
            if (c != ' ') {
                frequency.put(c, frequency.getOrDefault(c, 0) + 1);
            }
        }
        return frequency;
    }


    public boolean exists(String value) {
        return analyserRepo.findByValue(value).isPresent();
    }
}
