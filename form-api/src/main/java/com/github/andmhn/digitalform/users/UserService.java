package com.github.andmhn.digitalform.users;

import com.github.andmhn.digitalform.exeptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean hasUserWithEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getValidUserWithEmailAndPassword(String email, String password) throws UnauthorizedException{
        return getUserByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new UnauthorizedException(
                        String.format("Bad Credential -> Email: %s Password: %s", email, password)
                ));
    }

    public User getValidUserWithEmail(String email) throws UnauthorizedException {
        return getUserByEmail(email).orElseThrow(() -> new UnauthorizedException(
                String.format("No such user with email %s", email))
        );
    }
}