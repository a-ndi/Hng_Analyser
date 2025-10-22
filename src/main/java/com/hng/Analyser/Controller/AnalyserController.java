package com.hng.Analyser.Controller;


import com.hng.Analyser.Model.AnalysedString;
import com.hng.Analyser.Service.errors.BadRequestException;
import com.hng.Analyser.Service.errors.InvalidDataTypeException;
import com.hng.Analyser.Service.errors.ResourceConflictException;
import com.hng.Analyser.Service.errors.ResourceNotFoundException;
import com.hng.Analyser.Service.model.AnalyseRequestBody;
import com.hng.Analyser.Service.AnalyserService;
import com.hng.Analyser.Service.helper.NaturalLanguageParser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AnalyserController {
    private final AnalyserService analyserService;

    public AnalyserController(AnalyserService analyserService) {
        this.analyserService = analyserService;
    }

    @PostMapping("/strings")
    public ResponseEntity<?> analyse(@Valid @RequestBody AnalyseRequestBody inputString) {

        if (inputString == null || inputString.getValue() == null) {
            throw new BadRequestException("Invalid request body or missing 'value' field");
        }

        String value = inputString.getValue().trim();

        if (value.isEmpty()) {
            throw new BadRequestException("'value' cannot be empty");
        }

        // reject invalid types (e.g., number disguised as string)
        if (!value.matches("^[A-Za-z\\s]+$")) {
            throw new InvalidDataTypeException("Invalid data type for 'value' (must be string)");
        }


        // check for duplicates
        if (analyserService.exists(value)) {
            throw new ResourceConflictException("String already exists in the system");
        }

        AnalysedString saved = analyserService.analyse(value);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/strings")
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) Boolean is_palindrome,
            @RequestParam(required = false) Integer min_length,
            @RequestParam(required = false) Integer max_length,
            @RequestParam(required = false) Integer word_count,
            @RequestParam(required = false) String contains_character
    ) {
        // basic validation of query params
        if (min_length != null && min_length < 0) return ResponseEntity.badRequest().body(Map.of("error","min_length must be >= 0"));
        if (max_length != null && max_length < 0) return ResponseEntity.badRequest().body(Map.of("error","max_length must be >= 0"));

        List<AnalysedString> filtered = analyserService.filter(is_palindrome, min_length, max_length, word_count, contains_character);
        Map<String, Object> resp = new HashMap<>();
        resp.put("data", filtered);
        resp.put("count", filtered.size());
        Map<String, Object> filters = new HashMap<>();
        filters.put("is_palindrome", is_palindrome);
        filters.put("min_length", min_length);
        filters.put("max_length", max_length);
        filters.put("word_count", word_count);
        filters.put("contains_character", contains_character);
        resp.put("filters_applied", filters);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/strings/{string_value}")
    public ResponseEntity<AnalysedString> getByValue(@PathVariable String string_value) {
        Optional<AnalysedString> found = analyserService.getByValue(string_value);
        if (found.isPresent()){
            return ResponseEntity.ok(found.get());
        } else {
            throw new ResourceNotFoundException("String does not exist in the system");
        }
    }

    @GetMapping("/strings/filter-by-natural-language")
    public ResponseEntity<?> naturalLanguageFilter(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "query is required"));
        }

        NaturalLanguageParser.ParseResult parsed;
        try {
            parsed = NaturalLanguageParser.parse(query);
        } catch (Exception e) {
            throw new BadRequestException("Unable to parse natural language query");
        }

        // convert parsed filters to service filter call
        Boolean is_pal = (Boolean) parsed.parsedFilters.getOrDefault("is_palindrome", null);
        Integer minLen = (Integer) parsed.parsedFilters.getOrDefault("min_length", null);
        Integer maxLen = (Integer) parsed.parsedFilters.getOrDefault("max_length", null);

        if (minLen != null && maxLen != null && minLen > maxLen) {
            // Corresponds to 422 Unprocessable Entity: Query parsed but resulted in conflicting filters
            throw new InvalidDataTypeException("Query parsed but resulted in conflicting filters");
        }

        Integer wc = (Integer) parsed.parsedFilters.getOrDefault("word_count", null);
        String contains = (String) parsed.parsedFilters.getOrDefault("contains_character", null);

        List<AnalysedString> data = analyserService.filter(is_pal, minLen, maxLen, wc, contains);
        Map<String,Object> resp = new HashMap<>();
        resp.put("data", data);
        resp.put("count", data.size());
        Map<String,Object> interpreted = new HashMap<>();
        interpreted.put("original", query);
        interpreted.put("parsed_filters", parsed.parsedFilters);
        resp.put("interpreted_query", interpreted);
        return ResponseEntity.ok(resp);
    }

    // 5. Delete string by value
    @DeleteMapping("/strings/{string_value}")
    public ResponseEntity<Void> delete(@PathVariable String stringValue) {
        boolean deleted = analyserService.deleteByValue(stringValue);
        if (!deleted) {
            throw new ResourceNotFoundException("String does not exist in the system");
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}


