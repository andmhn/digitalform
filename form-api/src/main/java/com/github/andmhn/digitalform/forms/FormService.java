package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.exeptions.ForbiddenException;
import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.exeptions.UnauthorizedException;
import com.github.andmhn.digitalform.forms.dto.*;
import com.github.andmhn.digitalform.users.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FormService {
    @Autowired
    private final FormRepository formRepository;

    @Autowired
    private final QuestionRepository questionRepository;

    private List<Question> saveQuestions(List<QuestionRequest> questionRequest) {
        return questionRequest.stream().map(Mapper::fromQuestionRequest).toList();
    }

    public Form update(Form form, FormUpdateRequest formRequest) {
        form.setHeader(formRequest.header());
        form.setDescription(formRequest.description());
        form.setUnlisted(formRequest.unlisted());
        form.setPublished(formRequest.published());
        return formRepository.save(form);
    }

    public Form saveFormRequestForUser(FormRequest formRequest, User user) {
        List<Question> savedQuestions = saveQuestions(formRequest.questions());
        Form mappedForms = Form.builder()
                .user(user)
                .description(formRequest.description())
                .unlisted(formRequest.unlisted())
                .header(formRequest.header())
                .published(formRequest.published())
                .questions(savedQuestions)
                .build();
        return formRepository.save(mappedForms);
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

    public FormResponse getById(UUID formId) throws NotFoundException {
        FormResponse savedForm = formRepository.findByIdDTO(formId)
                .orElseThrow(() -> new NotFoundException("No Such Form with form id: " + formId));
        return injectQuestions(savedForm);
    }

    public List<SubmissionResponse> getAllSubmissionsOfForm(User currentUser, UUID form_id) {
        Form form = getFormIfUserOwnsIt(currentUser, form_id);
        List<Submission> submissions = form.getSubmissions();
        return submissions.stream().map(Mapper::toSubmissionResponse).toList();
    }

    public Form getFormIfUserOwnsIt(User currentUser, UUID form_id) {
        Form form = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("No Such form"));
        User formUser = form.getUser();
        if (currentUser != formUser) {
            throw new ForbiddenException("User doesn't own form");
        }
        return form;
    }

    public void delete_form(User user, UUID form_id) throws UnauthorizedException {
        Form form = getFormIfUserOwnsIt(user, form_id);
        formRepository.delete(form);
    }

    public void writeFormResponses(Form form, Writer writer) throws IOException {
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
        List<Question> formQuestions = form.getQuestions();
        List<String> questionHeaders = formQuestions.stream().map(Question::getQuery).toList();
        printer.printRecord(questionHeaders);

        for (Submission submission : form.getSubmissions()) {
            for (Question currentQuestion : formQuestions) {
                String answerForQuestion = submission.getAnswers().stream()
                        .filter(answer -> answer.getQuestion().equals(currentQuestion))
                        .map(Answer::getAnswer)
                        .findFirst()
                        .orElse("");
                printer.print(answerForQuestion);
            }
            printer.println();
        }
    }
}
