package com.github.andmhn.digitalform.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SubmissionResponse {
    Long submission_id;
    Long form_id;
    List<AnswerResponse> answers;
}
