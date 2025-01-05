package com.github.andmhn.digitalform.controller;

import com.github.andmhn.digitalform.entity.User;
import com.github.andmhn.digitalform.security.CustomUserDetails;
import com.github.andmhn.digitalform.service.UserService;
import com.github.andmhn.digitalform.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @DeleteMapping("/me")
    public AuthResponse deleteUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = userService.getValidUserWithEmail(currentUser.getEmail());
        userService.deleteUser(user);
        return mapToAuthResponse(user);
    }

    private AuthResponse mapToAuthResponse(User user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail());
    }
}
