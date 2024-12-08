package com.github.andmhn.digitalform.forms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record QuestionResponse (
        Long         question_id,
        String       query,
        boolean      required,
        String       type,
        List<String> choices
) {

}
