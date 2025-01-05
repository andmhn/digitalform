package com.github.andmhn.digitalform.dto;

public record FormUpdateRequest(
        String header,
        String description,
        Boolean unlisted,
        Boolean published
) {
}
