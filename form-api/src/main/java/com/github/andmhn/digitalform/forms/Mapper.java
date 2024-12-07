package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.FormResponse;
import com.github.andmhn.digitalform.forms.dto.QuestionRequest;
import com.github.andmhn.digitalform.forms.dto.QuestionResponse;

public class Mapper {
    public static QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .query(question.getQuery())
                .required(question.isRequired())
                .type(question.getType())
                .choices(question.getChoices())
                .build();
    }

    public static Question fromQuestionRequest(QuestionRequest questionRequest) {
        return Question.builder()
                .query(questionRequest.query())
                .type(questionRequest.type())
                .choices(questionRequest.choices())
                .required(questionRequest.required())
                .build();
    }

    public static FormResponse toFormResponse(Form form) {
        return FormResponse.builder()
                .id(form.getId())
                .header(form.getHeader())
                .description(form.getDescription())
                .unlisted(form.getUnlisted())
                .questions(form.getQuestions().stream().map(Mapper::toQuestionResponse).toList())
                .build();
    }
}
