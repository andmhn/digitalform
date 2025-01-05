package com.github.andmhn.digitalform.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FormRequest(
        String header,
        String description,
        Boolean unlisted,
        Boolean published,
        List<QuestionRequest> questions
) {
}
