package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.exeptions.NotFoundException;
import com.github.andmhn.digitalform.exeptions.UnauthorizedException;
import com.github.andmhn.digitalform.forms.dto.QuestionRequest;
import com.github.andmhn.digitalform.forms.dto.QuestionResponse;
import com.github.andmhn.digitalform.security.CustomUserDetails;
import com.github.andmhn.digitalform.users.User;
import com.github.andmhn.digitalform.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class QuestionController {
    private final QuestionRepository questionRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final FormRepository formRepository;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/users/questions/add")
    public QuestionResponse addQuestion(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long form_id,
            @RequestBody QuestionRequest questionRequest
    ){
        User user = userService.getValidUserWithEmail(customUserDetails.getEmail());
        Form savedForm = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("Not Found"));
        boolean userOwnsTheForm = Objects.equals(savedForm.getFk_user(), user.getId());
        if(!userOwnsTheForm){
            throw new UnauthorizedException(
                    "User: " + user.getEmail() + " doesn't own containing form: " + savedForm.getId()
            );
        }
        Question question = Mapper.fromQuestionRequest(questionRequest, form_id);
        return Mapper.toQuestionResponse(questionRepository.save(question));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/users/questions/{id}")
    public QuestionResponse updateQuestion(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id,
            @RequestBody QuestionRequest questionRequest
            ){
        User user = userService.getValidUserWithEmail(customUserDetails.getEmail());
        Question savedQuestion = questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found"));
        boolean userOwnsTheForm = userOwnsContainingForm(savedQuestion.getFk_form(), user.getId());
        if(!userOwnsTheForm){
            throw new UnauthorizedException(
                    "User: " + user.getEmail() + " doesn't own containing form: "+savedQuestion.getFk_form()
            );
        }
        savedQuestion.setIndex(questionRequest.index());
        savedQuestion.setQuery(questionRequest.query());
        savedQuestion.setRequired(questionRequest.required());
        savedQuestion.setType(questionRequest.type());
        savedQuestion.setChoices(questionRequest.choices());
        Question updatedQuestion = questionRepository.update(savedQuestion);
        return Mapper.toQuestionResponse(updatedQuestion);
    }

    private boolean userOwnsContainingForm(Long form_id , Long user_id) {
        Form savedform = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("Not Found"));
        return savedform.getFk_user() .equals(user_id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/users/questions/{id}")
    public void deleteQuestion(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id
    ){
        User user = userService.getValidUserWithEmail(customUserDetails.getEmail());
        Question savedQuestion = questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found"));
        boolean userOwnsTheForm = userOwnsContainingForm(savedQuestion.getFk_form(), user.getId());
        if(!userOwnsTheForm){
            throw new UnauthorizedException(
                    "User: " + user.getEmail() + " doesn't own containing form: "+savedQuestion.getFk_form()
            );
        }
        questionRepository.delete(savedQuestion);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/public/questions/{id}")
    public QuestionResponse getQuestion(
            @PathVariable Long id
    ){
        Question savedQuestion = questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Not Found"));
        return Mapper.toQuestionResponse(savedQuestion);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/public/questions")
    public List<QuestionResponse> getAllQuestion(
            @RequestParam Long form_id
    ){
        Form form = formRepository.findById(form_id).orElseThrow(() -> new NotFoundException("Not Found"));
        return questionRepository.getAllByFormDTO(form);
    }
}
