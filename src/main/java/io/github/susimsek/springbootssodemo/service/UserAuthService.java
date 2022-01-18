package io.github.susimsek.springbootssodemo.service;

import io.github.susimsek.springbootssodemo.dto.GenericResponse;
import io.github.susimsek.springbootssodemo.dto.SuccessFailure;
import io.github.susimsek.springbootssodemo.dto.Token;
import io.github.susimsek.springbootssodemo.model.User;
import io.github.susimsek.springbootssodemo.repository.UserRepository;
import io.github.susimsek.springbootssodemo.security.TokenProvider;
import io.github.susimsek.springbootssodemo.security.UserPrincipal;
import io.github.susimsek.springbootssodemo.util.CookieUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserAuthService implements UserDetailsService {

    final UserRepository userRepository;
    final TokenProvider tokenProvider;
    final CookieUtil cookieUtil;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(s).orElseThrow(() -> new UsernameNotFoundException("User not found with email " + s));
        return UserPrincipal.create(user);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        UserDetails userDetails = loadUserById(Long.parseLong(tokenProvider.getSubject(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return UserPrincipal.create(user);
    }

    public GenericResponse login(HttpHeaders responseHeaders, UserPrincipal user, String accessToken, String refreshToken) {
        boolean accessTokenValid = tokenProvider.validateToken(accessToken);
        boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);
        Token newAccessToken;
        Token newRefreshToken;
        if (!accessTokenValid && !refreshTokenValid) {
            newAccessToken = tokenProvider.generateToken(user);
            newRefreshToken = tokenProvider.generateRefreshToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        if (!accessTokenValid && refreshTokenValid) {
            newAccessToken = tokenProvider.generateToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
        }

        if (accessTokenValid && refreshTokenValid) {
            newAccessToken = tokenProvider.generateToken(user);
            newRefreshToken = tokenProvider.generateRefreshToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        return new GenericResponse(SuccessFailure.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
    }


    public GenericResponse refresh(HttpHeaders responseHeaders, String refreshToken) {
        boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);
        if (!refreshTokenValid) {
            return new GenericResponse(SuccessFailure.FAILURE, "Invalid refresh token !");
        }

        UserPrincipal user = (UserPrincipal) loadUserById(Long.parseLong(tokenProvider.getSubject(refreshToken)));

        Token newAccessToken = tokenProvider.generateToken(user);
        addAccessTokenCookie(responseHeaders, newAccessToken);

        return new GenericResponse(SuccessFailure.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(token.tokenValue(), token.duration()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createRefreshTokenCookie(token.tokenValue(), token.duration()).toString());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        removeCookieToken(request, response);
    }

    private void removeCookieToken(HttpServletRequest req, HttpServletResponse response) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Arrays.stream(req.getCookies())
                    .forEach(cookie -> {
                        cookieUtil.deleteTokenCookie(cookie);
                        response.addCookie(cookie);
                    });
        }
    }
}