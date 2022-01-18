package io.github.susimsek.springbootssodemo.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    String accessTokenCookieName;
    String refreshTokenCookieName;
    String tokenSecret;
    Long tokenExpirationMs;
    Long refreshTokenExpirationMs;
}