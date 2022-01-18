package io.github.susimsek.springbootssodemo.dto;

import java.time.LocalDateTime;

public record Token(
        TokenType tokenType,
        String tokenValue,
        Long duration,
        LocalDateTime expiryDate
) {
}
