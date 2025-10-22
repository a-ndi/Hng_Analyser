package com.hng.Analyser.Model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
public class AnalysedString {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Getter
    @Column(unique=true, nullable = false)
    private String value; //` value;

    @Setter
    @Getter
    private String created_at;

    @Setter
    @Getter
    @Transient
    private AnalysedStringProperties properties;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String propertiesJson;

    @PrePersist
    @PreUpdate
    private void convertMapToJson() {
        if (properties != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                propertiesJson = mapper.writeValueAsString(properties);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting map to JSON", e);
            }
        }
    }

    @PostLoad
    private void convertJsonToMap() {
        if (propertiesJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                properties = mapper.readValue(propertiesJson, AnalysedStringProperties.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting JSON to AnalysedStringProperties", e);
            }
        }
    }


    public AnalysedString() {}

}
