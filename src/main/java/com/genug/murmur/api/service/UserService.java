package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.exception.UserNotFoundException;
import com.genug.murmur.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(final String email, final String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        throw new UserNotFoundException();
    }


}
