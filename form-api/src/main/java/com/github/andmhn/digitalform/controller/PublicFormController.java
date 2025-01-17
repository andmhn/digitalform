package com.github.andmhn.digitalform.controller;

import com.github.andmhn.digitalform.exeptions.ForbiddenException;
import com.github.andmhn.digitalform.service.FormService;
import com.github.andmhn.digitalform.service.SubmissionService;
import com.github.andmhn.digitalform.dto.AnswerRequest;
import com.github.andmhn.digitalform.dto.FormResponse;
import com.github.andmhn.digitalform.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/forms")
@RequiredArgsConstructor
public class PublicFormController {
    @Autowired
    private final FormService formService;

    @Autowired
    private final SubmissionService submissionService;

    @GetMapping
    public ResponseEntity<FormResponse> getFormById(@RequestParam Long form_id) {
        FormResponse form = formService.getById(form_id);
        if(!form.getPublished()){
            throw new ForbiddenException("Forbidden to access");
        }
        return ResponseEntity.ok(form);
    }

    @GetMapping("/info")
    public ResponseEntity<List<FormResponse>> getAllPublicFormsInfo() {
        List<FormResponse> publicForms = formService.getAllPublicFormsInfo();
        return ResponseEntity.ok(publicForms);
    }

    @GetMapping("/data")
    public ResponseEntity<List<FormResponse>> getAllPublicFormsData() {
        List<FormResponse> publicForms = formService.getAllPublicFormsData();
        return ResponseEntity.ok(publicForms);
    }

    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public SubmissionResponse handleSubmission(@RequestParam Long form_id, @RequestBody List<AnswerRequest> answers) {
        return submissionService.saveSubmissionOfForm(form_id, answers);
    }
}
