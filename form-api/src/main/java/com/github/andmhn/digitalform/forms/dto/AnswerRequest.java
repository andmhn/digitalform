package com.github.andmhn.digitalform.forms.dto;

public record AnswerRequest (
    Long question_id,
    String answer
){
}
