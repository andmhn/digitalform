package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.exeptions.BadRequestException;
import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.forms.dto.AnswerRequest;
import com.github.andmhn.digitalform.forms.dto.FormResponse;
import com.github.andmhn.digitalform.forms.dto.QuestionResponse;
import com.github.andmhn.digitalform.forms.dto.SubmissionResponse;
import com.github.andmhn.digitalform.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    @Autowired
    private final SubmissionRepository submissionRepository;

    @Autowired
    private final FormRepository formRepository;

    @Autowired
    private final QuestionRepository questionRepository;

    @Autowired
    private final AnswerRepository answerRepository;

    public Submission getSubmission(Long id) {
        return submissionRepository.findById(id).orElseThrow(() -> new NotFoundException("No such Submission: " + id));
    }

    public void deleteSubmission(Submission submission) {
        submissionRepository.delete(submission);
    }

    public SubmissionResponse saveSubmissionOfForm(Long form_id, List<AnswerRequest> answers) {
        Form currentForm = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("No Such Form: " + form_id));
        List<QuestionResponse> allQuestionsInForm = questionRepository.getAllByFormDTO(currentForm);
        validateAnswersOrThrow(answers, allQuestionsInForm);
        Long submissionId = submissionRepository.save(new Submission(null, currentForm.getId())).getId();

        List<Answer> answerList = new ArrayList<>();
        for (AnswerRequest answer : answers) {
            answerList.add(
                    answerRepository.save(new Answer(null, answer.answer(), answer.question_id(), submissionId))
            );
        }
        return SubmissionResponse.builder()
                .submission_id(submissionId)
                .form_id(currentForm.getId())
                .answers(answerList.stream().map(Mapper::toAnswerResponse).toList())
                .build();
    }

    public boolean userOwnsContainingForm(Submission submission, User currentUser) {
        Optional<FormResponse> savedForm = formRepository.findByIdDTO(submission.getFk_form());
        if (savedForm.isPresent())
            return savedForm.get().getOwner_email().equals(currentUser.getEmail());
        return false;
    }

    private static void validateAnswersOrThrow(List<AnswerRequest> answers, List<QuestionResponse> allQuestionInForm) {
        List<QuestionResponse> requiredQuestions = allQuestionInForm.stream().filter(QuestionResponse::required).toList();
        for (QuestionResponse q : requiredQuestions) {
            answers.stream().filter(r -> r.question_id().equals(q.question_id())).findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "required question is not answered: " + q.question_id() + " -> " + q.query()
                    ));
        }

        for (AnswerRequest answer : answers) {
            allQuestionInForm.stream()
                    .filter(q -> q.question_id().equals(answer.question_id()))
                    .findFirst()
                    .orElseThrow(
                            () -> new BadRequestException("Current form doesn't contain Question: " + answer.question_id()
                            ));
        }
    }
}
