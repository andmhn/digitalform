package com.github.andmhn.digitalform.service;

import com.github.andmhn.digitalform.dto.*;
import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.exeptions.UnauthorizedException;
import com.github.andmhn.digitalform.repository.AnswerRepository;
import com.github.andmhn.digitalform.repository.FormRepository;
import com.github.andmhn.digitalform.repository.QuestionRepository;
import com.github.andmhn.digitalform.repository.SubmissionRepository;
import com.github.andmhn.digitalform.entity.Answer;
import com.github.andmhn.digitalform.entity.Form;
import com.github.andmhn.digitalform.entity.Question;
import com.github.andmhn.digitalform.entity.Submission;
import com.github.andmhn.digitalform.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormService {
    @Autowired
    private final FormRepository formRepository;

    @Autowired
    private final QuestionRepository questionRepository;

    @Autowired
    private final SubmissionRepository submissionRepository;

    @Autowired
    private final AnswerRepository answerRepository;

    public Form update(Form form, FormUpdateRequest formRequest) {
        form.setHeader(formRequest.header());
        form.setDescription(formRequest.description());
        form.setUnlisted(formRequest.unlisted());
        form.setPublished(formRequest.published());
        if(form.getPublished() == null)
            form.setPublished(false);
        return formRepository.update(form);
    }

    public FormResponse saveFormRequestForUser(FormRequest formRequest, User user) {
        Form mappedForms = Form.builder()
                .description(formRequest.description())
                .unlisted(formRequest.unlisted())
                .header(formRequest.header())
                .published(formRequest.published())
                .fk_user(user.getId())
                .build();
        if(mappedForms.getPublished() == null)
            mappedForms.setPublished(false);
        Form savedForm = formRepository.save(mappedForms);
        List<Question> savedQuestions = formRequest.questions().stream()
                .map( q -> Mapper.fromQuestionRequest(q, savedForm.getId()))
                .map(questionRepository::save).toList();
        return Mapper.toFormResponse(savedForm,savedQuestions, user.getEmail());
    }

    private FormResponse injectQuestions(FormResponse formResponse) {
        formResponse.setQuestions(
                questionRepository.getAllByFormDTO(
                        Form.builder().id(formResponse.getForm_id()).build()
                )
        );
        return formResponse;
    }

    public List<FormResponse> getAllUserFormsInfo(User user, Boolean published) {
        if(published == null)
            return formRepository.findAllByUserDTO(user);
        else {
            return formRepository.findAllPublishedByUserDTO(user, published);
        }
    }

    public List<FormResponse> getAllUserFormsData(User user) {
        return getAllUserFormsInfo(user, null).stream().map(this::injectQuestions).toList();
    }

    public List<FormResponse> getAllPublicFormsInfo() {
        return formRepository.findAllByUnlistedDTO(false);
    }

    public List<FormResponse> getAllPublicFormsData() {
        return getAllPublicFormsInfo().stream().map(this::injectQuestions).toList();
    }

    public FormResponse getById(Long formId) throws NotFoundException {
        FormResponse savedForm = formRepository.findByIdDTO(formId)
                .orElseThrow(() -> new NotFoundException("No Such Form with form id: " + formId));
        return injectQuestions(savedForm);
    }

    public List<SubmissionResponse> getAllSubmissionsOfForm(User currentUser, Long form_id) {
        getFormIfUserOwnsIt(currentUser, form_id); // TODO: make it efficient in repo with func (for every entity)
        List<Submission> submissions = submissionRepository.findByFormId(form_id);
        return submissions.stream().map(s -> {
            List<Answer> answers = answerRepository.findAllBySubmissionID(s.getId());
            return Mapper.toSubmissionResponse(s, form_id, answers);
        }).toList();
    }

    public Form getFormIfUserOwnsIt(User currentUser, Long form_id) {
        Form form = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("No Such form"));
        boolean isUserFormOwner = currentUser.getId().equals(form.getFk_user());
        if (!isUserFormOwner) {
            throw new UnauthorizedException("User doesn't own form");
        }
        return form;
    }

    public void delete_form(User user, Long form_id) throws UnauthorizedException {
        Form form = getFormIfUserOwnsIt(user, form_id);
        formRepository.delete(form);
    }

    public void writeFormResponses(Form form, Writer writer) throws IOException {
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
        List<QuestionResponse> formQuestions = questionRepository.getAllByFormDTO(form);
        List<String> questionHeaders = formQuestions.stream().map(QuestionResponse::query).toList();
        printer.printRecord(questionHeaders);

        List<Submission> submissions = submissionRepository.findByFormId(form.getId());
        for (Submission submission : submissions) {
            for (QuestionResponse currentQuestion : formQuestions) {
                List<Answer> answers = answerRepository.findAllBySubmissionID(submission.getId());
                String answerForQuestion = answers.stream()
                        .filter(answer -> answer.getFk_question().equals(currentQuestion.question_id()))
                        .map(Answer::getAnswer)
                        .findFirst()
                        .orElse("");
                printer.print(answerForQuestion);
            }
            printer.println();
        }
    }
}
