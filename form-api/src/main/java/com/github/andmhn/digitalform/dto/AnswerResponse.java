package com.github.andmhn.digitalform.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AnswerResponse {
    Long answer_id;
    Long question_id;
    String answer;
}
