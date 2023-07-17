package com.genug.murmur.api.service;

import com.genug.murmur.api.domain.User;
import com.genug.murmur.api.exception.UserNotFoundException;
import com.genug.murmur.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User login(final String email, final String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(UserNotFoundException::new);
    }


}
