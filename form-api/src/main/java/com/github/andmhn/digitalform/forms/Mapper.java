package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.*;

public class Mapper {
    public static QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .question_id(question.getId())
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
                .form_id(form.getId())
                .header(form.getHeader())
                .description(form.getDescription())
                .unlisted(form.getUnlisted())
                .questions(form.getQuestions().stream().map(Mapper::toQuestionResponse).toList())
                .build();
    }

    public static AnswerResponse toAnswerResponse(Answer answer){
        return AnswerResponse.builder()
                .answer_id(answer.getId())
                .question_id(answer.getQuestion().getId())
                .answer(answer.getAnswer())
                .build();
    }

    public static SubmissionResponse toSubmissionResponse(Submission submission) {
        return SubmissionResponse.builder()
                .submission_id(submission.getId())
                .form_id(submission.getForm().getId())
                .answers(submission.getAnswers().stream().map(Mapper::toAnswerResponse).toList())
                .build();
    }
}
