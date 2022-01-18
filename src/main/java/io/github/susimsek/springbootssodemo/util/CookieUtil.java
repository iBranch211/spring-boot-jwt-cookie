package io.github.susimsek.springbootssodemo.util;

import io.github.susimsek.springbootssodemo.security.AuthProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class CookieUtil {

    final AuthProperties authProperties;
    final SecurityCipher securityCipher;

    public HttpCookie createAccessTokenCookie(String token, Long duration) {
        String encryptedToken = securityCipher.encrypt(token);
        return ResponseCookie.from(authProperties.getAccessTokenCookieName(), encryptedToken)
                .maxAge(-1)
                .httpOnly(true)
                .path("/")
                .build();
    }

 

    public HttpCookie createRefreshTokenCookie(String token, Long duration) {
        String encryptedToken = securityCipher.encrypt(token);
        return ResponseCookie.from(authProperties.getRefreshTokenCookieName(), encryptedToken)
                .maxAge(-1)
                .httpOnly(true)
                .path("/")
                .build();
    }


    public void deleteTokenCookie(Cookie cookie) {
        cookie.setMaxAge(0);
        cookie.setValue("");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
    }
}