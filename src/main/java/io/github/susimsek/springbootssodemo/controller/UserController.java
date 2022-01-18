package io.github.susimsek.springbootssodemo.controller;


import io.github.susimsek.springbootssodemo.dto.UserProfile;
import io.github.susimsek.springbootssodemo.security.CurrentUser;
import io.github.susimsek.springbootssodemo.security.UserPrincipal;
import io.github.susimsek.springbootssodemo.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    @GetMapping("/api/1.0/me")
    public UserProfile me(@CurrentUser UserPrincipal user) {
        return userService.getUserProfile(user);
    }
}