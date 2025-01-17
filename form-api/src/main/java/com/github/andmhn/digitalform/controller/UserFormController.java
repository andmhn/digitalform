package com.github.andmhn.digitalform.controller;

import com.github.andmhn.digitalform.dto.FormRequest;
import com.github.andmhn.digitalform.dto.FormResponse;
import com.github.andmhn.digitalform.dto.FormUpdateRequest;
import com.github.andmhn.digitalform.dto.SubmissionResponse;
import com.github.andmhn.digitalform.exeptions.ForbiddenException;
import com.github.andmhn.digitalform.exeptions.UnauthorizedException;
import com.github.andmhn.digitalform.service.FormService;
import com.github.andmhn.digitalform.service.Mapper;
import com.github.andmhn.digitalform.service.SubmissionService;
import com.github.andmhn.digitalform.entity.Form;
import com.github.andmhn.digitalform.entity.Submission;
import com.github.andmhn.digitalform.security.CustomUserDetails;
import com.github.andmhn.digitalform.entity.User;
import com.github.andmhn.digitalform.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/forms")
public class UserFormController {
    @Autowired
    private final FormService formService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final SubmissionService submissionService;

    @GetMapping
    public ResponseEntity<FormResponse> getFormById(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam Long form_id
    ) {
        FormResponse form = formService.getById(form_id);
        if(form.getPublished()){
            return ResponseEntity.ok(form);
        }
        else if(!Objects.equals(form.getOwner_email(), currentUser.getEmail())){
            throw new ForbiddenException("Forbidden to access");
        }
        return ResponseEntity.ok(form);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormResponse createNewForm(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody FormRequest formRequest
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        return formService.saveFormRequestForUser(formRequest, user);
    }

    @GetMapping("/info")
    public ResponseEntity<List<FormResponse>> getAllUserFormsInfo(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) Boolean published
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        List<FormResponse> userForms = formService.getAllUserFormsInfo(user, published);
        return ResponseEntity.ok(userForms);
    }

    // to be deleted
    @GetMapping("/data")
    public ResponseEntity<List<FormResponse>> getAllUserFormsData(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        List<FormResponse> userForms = formService.getAllUserFormsData(user);
        return ResponseEntity.ok(userForms);
    }

    @GetMapping("/submissions")
    @ResponseStatus(HttpStatus.OK)
    public List<SubmissionResponse> getAllSubmissions(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam Long form_id
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        return formService.getAllSubmissionsOfForm(user, form_id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteForm(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam Long form_id
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        formService.delete_form(user, form_id);
    }

    @GetMapping("/export")
    public void exportIntoCSV(HttpServletResponse response,
                              @AuthenticationPrincipal CustomUserDetails currentUser,
                              @RequestParam Long form_id
    ) throws IOException {
        User validUserWithEmail = userService.getValidUserWithEmail(currentUser.getEmail());
        Form form = formService.getFormIfUserOwnsIt(validUserWithEmail, form_id);

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        response.setContentType("text/csv");
        response.addHeader(
                "Content-Disposition",
                "attachment; filename=\"" + form.getHeader() + " [" + currentTime + "].csv\""
        );
        formService.writeFormResponses(form, response.getWriter());
    }

    @DeleteMapping("/submit/{submissionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSubmission(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long submissionId
    ){
        User currentUser = userService.getValidUserWithEmail(customUserDetails.getEmail());
        Submission submission = submissionService.getSubmission(submissionId);
        boolean userOwnsTheForm = submissionService.userOwnsContainingForm(submission, currentUser);
        if (!userOwnsTheForm)
            throw new UnauthorizedException("User Is Not Owner Of Form");
        submissionService.deleteSubmission(submission);
    }

    @PatchMapping
    public ResponseEntity<FormResponse> updateFormById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long form_id,
            @RequestBody FormUpdateRequest formRequest
    ) {
        User user = userService.getValidUserWithEmail(customUserDetails.getEmail());
        Form form = formService.getFormIfUserOwnsIt(user, form_id);
        form = formService.update(form, formRequest);
        FormResponse res = Mapper.toFormResponse(form, new ArrayList<>(), user.getEmail());
        res.setQuestions(null);
        return ResponseEntity.ok(res);
    }
}
