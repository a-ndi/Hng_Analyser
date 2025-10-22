package com.hng.Analyser.Model;


import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.util.Map;

@Setter
@Getter
public class AnalysedStringProperties {

    private int length;

    @ColumnDefault("true")
    private boolean is_palindrome;

    private int word_count;

    private String unique_characters;

    private String sha256_hash;

    private Map<Character, Integer> character_frequency_map;

    public AnalysedStringProperties() {}

}
