package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.FormRequest;
import com.github.andmhn.digitalform.forms.dto.FormResponse;
import com.github.andmhn.digitalform.security.CustomUserDetails;
import com.github.andmhn.digitalform.users.User;
import com.github.andmhn.digitalform.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/forms")
public class UserFormController {
    @Autowired
    private final FormService formService;

    @Autowired
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormResponse createNewForm(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody FormRequest formRequest
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        Form savedForm = formService.saveFormRequestForUser(formRequest, user);
        return Mapper.toFormResponse(savedForm);
    }

    @GetMapping("/info")
    public ResponseEntity<List<FormResponse>> getAllUserFormsInfo(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        List<FormResponse> userForms = formService.getAllUserFormsInfo(user);
        return ResponseEntity.ok(userForms);
    }

    @GetMapping("/data")
    public ResponseEntity<List<FormResponse>> getAllUserFormsData(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        List<FormResponse> userForms = formService.getAllUserFormsData(user);
        return ResponseEntity.ok(userForms);
    }
}
