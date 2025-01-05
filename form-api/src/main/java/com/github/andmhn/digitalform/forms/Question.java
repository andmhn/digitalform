package com.github.andmhn.digitalform.forms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;

    private Integer index;
    private String query;
    private boolean required;
    private String type;
    private String[] choices;

    private Long fk_form;
}