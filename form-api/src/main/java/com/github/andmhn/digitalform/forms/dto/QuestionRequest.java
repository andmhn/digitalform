package com.github.andmhn.digitalform.forms.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record QuestionRequest(
        String       query,
        boolean      required,
        String       type,
        List<String> choices
) {
}
