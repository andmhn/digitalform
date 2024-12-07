package com.github.andmhn.digitalform.forms.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormResponse{
    UUID                    id;
    String                  header;
    String                  description;
    Boolean                 unlisted;
    List<QuestionResponse>  questions;

    // made for querying in repository
    public FormResponse(UUID id, String header, String description, Boolean unlisted) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.unlisted = unlisted;
    }
}
