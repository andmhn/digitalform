package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.FormResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/forms")
@RequiredArgsConstructor
public class PublicFormController {
    @Autowired
    private final FormService formService;

    @GetMapping
    public ResponseEntity<FormResponse> getFormById(@RequestParam UUID id) {
        FormResponse form = formService.getById(id);
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

    // TODO: handle submission here
}
