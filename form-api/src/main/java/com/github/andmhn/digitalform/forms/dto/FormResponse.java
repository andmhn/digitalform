package com.github.andmhn.digitalform.forms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FormResponse{
    Long                    form_id;
    String                  header;
    String                  description;
    Boolean                 unlisted;
    Boolean                 published;
    String                  owner_email;
    List<QuestionResponse>  questions;

    // made for querying in repository
    public FormResponse(Long form_id, String header, String description, Boolean unlisted, Boolean published, String owner_email) {
        this.form_id = form_id;
        this.header = header;
        this.description = description;
        this.published = published;
        this.unlisted = unlisted;
        this.owner_email = owner_email;
    }
}
