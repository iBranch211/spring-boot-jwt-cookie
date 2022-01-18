package io.github.susimsek.springbootssodemo.service;

import io.github.susimsek.springbootssodemo.dto.UserProfile;
import io.github.susimsek.springbootssodemo.model.User;
import io.github.susimsek.springbootssodemo.repository.UserRepository;
import io.github.susimsek.springbootssodemo.security.UserPrincipal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public long count() {
        return userRepository.count();
    }

    public UserProfile getUserProfile(UserPrincipal user) {
        return new UserProfile(user);
    }
}
