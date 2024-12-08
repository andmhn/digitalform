package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.forms.dto.FormRequest;
import com.github.andmhn.digitalform.forms.dto.FormResponse;
import com.github.andmhn.digitalform.forms.dto.QuestionRequest;
import com.github.andmhn.digitalform.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Question> mappedQuestions = questionRequest.stream().map(Mapper::fromQuestionRequest).toList();
        //return questionRepository.saveAll(mappedQuestions);
        return mappedQuestions;
    }

    public Form saveFormRequestForUser(FormRequest formRequest, User user) {
        List<Question> savedQuestions = saveQuestions(formRequest.questions());
        Form mappedForms = Form.builder()
                .user(user)
                .description(formRequest.description())
                .unlisted(formRequest.unlisted())
                .header(formRequest.header())
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

    public List<FormResponse> getAllUserFormsInfo(User user) {
        return formRepository.findAllByUserDTO(user);
    }

    public List<FormResponse> getAllUserFormsData(User user) {
        return getAllUserFormsInfo(user).stream().map(this::injectQuestions).toList();
    }

    public List<FormResponse> getAllPublicFormsInfo() {
        return formRepository.findAllByUnlistedDTO(false);
    }

    public List<FormResponse> getAllPublicFormsData() {
        return getAllPublicFormsInfo().stream().map(this::injectQuestions).toList();
    }

    public FormResponse getById(UUID formId) throws NotFoundException {
        FormResponse savedForm = formRepository.findByIdDTO(formId)
                .orElseThrow(() -> new NotFoundException("No Such Form with submission_id: " + formId));
        return injectQuestions(savedForm);
    }
}
