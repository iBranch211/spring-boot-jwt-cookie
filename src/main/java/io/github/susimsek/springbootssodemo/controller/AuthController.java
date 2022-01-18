package io.github.susimsek.springbootssodemo.controller;

import io.github.susimsek.springbootssodemo.dto.GenericResponse;
import io.github.susimsek.springbootssodemo.dto.LoginRequest;
import io.github.susimsek.springbootssodemo.dto.SuccessFailure;
import io.github.susimsek.springbootssodemo.security.UserPrincipal;
import io.github.susimsek.springbootssodemo.service.UserAuthService;
import io.github.susimsek.springbootssodemo.util.SecurityCipher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("/api/1.0")
public class AuthController {

    final UserAuthService userAuthService;
    final AuthenticationManager authenticationManager;
    final SecurityCipher securityCipher;

    @PostMapping("/login")
    ResponseEntity<GenericResponse> handleLogin(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @Valid @RequestBody LoginRequest credentials) {
        String decryptedAccessToken = securityCipher.decrypt(accessToken);
        String decryptedRefreshToken = securityCipher.decrypt(refreshToken);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        HttpHeaders responseHeaders = new HttpHeaders();
        GenericResponse response = userAuthService.login(responseHeaders, user, decryptedAccessToken, decryptedRefreshToken);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(response);
    }

    @PostMapping("/refresh")
    ResponseEntity<GenericResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        String decryptedRefreshToken = securityCipher.decrypt(refreshToken);
        HttpHeaders responseHeaders = new HttpHeaders();
        GenericResponse response =  userAuthService.refresh(responseHeaders, decryptedRefreshToken);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<GenericResponse> logOut(HttpServletRequest req, HttpServletResponse resp) {
        SecurityContextHolder.clearContext();
        userAuthService.logout(req, resp);
        GenericResponse response = new GenericResponse(SuccessFailure.SUCCESS, "Logout successfully.");
        return ResponseEntity.ok()
                .body(response);
    }
}
