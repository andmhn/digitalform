package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.exeptions.BadRequestException;
import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.forms.dto.AnswerRequest;
import com.github.andmhn.digitalform.forms.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    @Autowired
    private final SubmissionRepository submissionRepository;

    @Autowired
    private final FormRepository formRepository;

    public SubmissionResponse handleSubmissionOfForm(UUID form_id, List<AnswerRequest> answers){
        Form currentForm = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("No Such Form: " + form_id));

        List<Question> allQuestionsInForm = currentForm.getQuestions();
        checkForRequiredAnswersOrThrow(answers, allQuestionsInForm);

        List <Answer> answerList = injectQuestionIdToAnswer(allQuestionsInForm, answers);

        Submission submission = Submission.builder()
                .form(currentForm)
                .answers(answerList).build();

        submission = submissionRepository.save(submission);

        return Mapper.toSubmissionResponse(submission);
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
