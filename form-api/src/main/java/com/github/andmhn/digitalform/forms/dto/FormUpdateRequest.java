package com.github.andmhn.digitalform.forms.dto;

public record FormUpdateRequest(
        String header,
        String description,
        Boolean unlisted,
        Boolean published
) {
}
