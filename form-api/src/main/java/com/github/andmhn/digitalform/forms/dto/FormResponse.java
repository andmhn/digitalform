package com.github.andmhn.digitalform.forms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FormResponse{
    UUID                    form_id;
    String                  header;
    String                  description;
    Boolean                 unlisted;
    String                  owner_email;
    List<QuestionResponse>  questions;

    // made for querying in repository
    public FormResponse(UUID form_id, String header, String description, Boolean unlisted, String owner_email) {
        this.form_id = form_id;
        this.header = header;
        this.description = description;
        this.unlisted = unlisted;
        this.owner_email = owner_email;
    }
}
