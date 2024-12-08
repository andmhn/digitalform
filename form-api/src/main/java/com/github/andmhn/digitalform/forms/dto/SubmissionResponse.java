package com.github.andmhn.digitalform.forms.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class SubmissionResponse {
    Long submission_id;
    UUID formId;
    List<AnswerResponse> answers;
}
