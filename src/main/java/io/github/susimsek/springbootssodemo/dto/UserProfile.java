package io.github.susimsek.springbootssodemo.dto;

import io.github.susimsek.springbootssodemo.security.UserPrincipal;

public record UserProfile(
        long id,
        String username,
        String email
) {

    public UserProfile(UserPrincipal user) {
        this(user.id(), user.username(), user.email());
    }
}
