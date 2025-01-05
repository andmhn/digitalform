package com.github.andmhn.digitalform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    private Long id;
    private String answer;

    private Long fk_question;
    private Long fk_submission;
}
