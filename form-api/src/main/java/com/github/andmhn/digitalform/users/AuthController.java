package com.github.andmhn.digitalform.users;

import com.github.andmhn.digitalform.exeptions.ConflictException;
import com.github.andmhn.digitalform.users.dto.AuthResponse;
import com.github.andmhn.digitalform.users.dto.LoginRequest;
import com.github.andmhn.digitalform.users.dto.SignUpRequest;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public AuthResponse signUp(@RequestBody SignUpRequest signUpRequest) {
        if (userService.hasUserWithEmail(signUpRequest.getEmail())) {
            throw new ConflictException(
                    String.format("Email %s is already been used", signUpRequest.getEmail())
            );
        }
        User user = userService.saveUser(createUser(signUpRequest));
        return new AuthResponse(user.getId(), user.getEmail(), user.getName());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody LoginRequest loginRequest) {
        User authUser = userService.getValidUserWithEmailAndPassword(
                loginRequest.getEmail(), loginRequest.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(
                authUser.getId(), authUser.getEmail(), authUser.getName()
        ));
    }

    private User createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setPassword(signUpRequest.getPassword());
        return user;
    }
}
