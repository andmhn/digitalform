package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.*;

import java.util.List;

public class Mapper {
    public static QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .question_id(question.getId())
                .index(question.getIndex())
                .query(question.getQuery())
                .required(question.isRequired())
                .type(question.getType())
                .choices(question.getChoices())
                .build();
    }

    public static Question fromQuestionRequest(QuestionRequest questionRequest, Long fk_form) {
        return Question.builder()
                .fk_form(fk_form)
                .index(questionRequest.index())
                .query(questionRequest.query())
                .type(questionRequest.type())
                .choices(questionRequest.choices())
                .required(questionRequest.required())
                .build();
    }

    public static FormResponse toFormResponse(Form form, List<Question> questions, String ownerEmail) {
        return FormResponse.builder()
                .form_id(form.getId())
                .header(form.getHeader())
                .description(form.getDescription())
                .unlisted(form.getUnlisted())
                .published(form.getPublished())
                .questions(questions.stream().map(Mapper::toQuestionResponse).toList())
                .owner_email(ownerEmail)
                .build();
    }

    public static AnswerResponse toAnswerResponse(Answer answer){
        return AnswerResponse.builder()
                .answer_id(answer.getId())
                .question_id(answer.getFk_question())
                .answer(answer.getAnswer())
                .build();
    }

    public static SubmissionResponse toSubmissionResponse(Submission submission, Long fk_form, List<Answer> answers) {
        return SubmissionResponse.builder()
                .submission_id(submission.getId())
                .form_id(fk_form)
                .answers(answers.stream().map(Mapper::toAnswerResponse).toList())
                .build();
    }
}
