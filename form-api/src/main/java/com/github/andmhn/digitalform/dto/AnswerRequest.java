package com.github.andmhn.digitalform.dto;

public record AnswerRequest (
    Long question_id,
    String answer
){
}
