package com.hng.Analyser.Service.model;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalyseRequestBody {

    @NotBlank(message = "'value' field cannot be blank")
    private String value;
}
