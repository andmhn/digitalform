package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.exeptions.BadRequestException;
import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.forms.dto.AnswerRequest;
import com.github.andmhn.digitalform.forms.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    @Autowired
    private final SubmissionRepository submissionRepository;

    @Autowired
    private final FormRepository formRepository;

    public Submission getSubmission(Long id) {
        return submissionRepository.findById(id).orElseThrow(() -> new NotFoundException("No such Submission: " + id) );
    }

    public void deleteSubmission(Submission submission) {
        submissionRepository.delete(submission);
    }

    public SubmissionResponse handleSubmissionOfForm(Long form_id, List<AnswerRequest> answers){
        Form currentForm = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("No Such Form: " + form_id));
        Submission submission = groupAnswersAsSubmission(answers, currentForm);
        submission = submissionRepository.save(submission);
        return Mapper.toSubmissionResponse(submission);
    }

    private static Submission groupAnswersAsSubmission(List<AnswerRequest> answers, Form currentForm) {
        List<Question> allQuestionsInForm = currentForm.getQuestions();
        checkForRequiredAnswersOrThrow(answers, allQuestionsInForm);

        List <Answer> answerList = injectQuestionIdToAnswer(allQuestionsInForm, answers);
        return Submission.builder()
                .form(currentForm)
                .answers(answerList).build();
    }

    private static List<Answer> injectQuestionIdToAnswer(List<Question> allQuestionInForm, List<AnswerRequest> answers) {
        return answers.stream().map(answerRequest -> Answer.builder()
                    .question(getMatchingQuestionOrThrow(allQuestionInForm, answerRequest))
                    .answer(answerRequest.answer())
                    .build()
        ).toList();
    }

    private static Question getMatchingQuestionOrThrow(List<Question> allQuestionInForm, AnswerRequest answerRequest) {
        return allQuestionInForm.stream()
                .filter(q -> q.getId().equals(answerRequest.question_id())).findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Current form doesn't contain Question: " + answerRequest.question_id()
                ));
    }

    private static void checkForRequiredAnswersOrThrow(List<AnswerRequest> answers, List<Question> allQuestionInForm) {
        for (Question q : allQuestionInForm.stream().filter(Question::isRequired).toList()){
            answers.stream().filter(r -> r.question_id().equals(q.getId())).findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "required question is not answered: " + q.getId() + " -> " + q.getQuery()
                    ));
        }
    }
}
