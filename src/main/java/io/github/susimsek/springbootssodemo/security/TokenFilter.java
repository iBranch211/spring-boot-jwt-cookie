package io.github.susimsek.springbootssodemo.security;

import io.github.susimsek.springbootssodemo.service.UserAuthService;
import io.github.susimsek.springbootssodemo.util.SecurityCipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenFilter extends OncePerRequestFilter {

    @Autowired
    AuthProperties authProperties;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    SecurityCipher securityCipher;

    @Autowired
    UserAuthService userAuthService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtToken(request, true);
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authentication = userAuthService.getAuthentication(jwt);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveCookieToken(
                authProperties.getAccessTokenCookieName(), request);
        if (accessToken == null) {
            return null;
        }
        return securityCipher.decrypt(accessToken);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken == null) {
            return null;
        }
        return securityCipher.decrypt(accessToken);
    }

    private String getJwtToken(HttpServletRequest request, boolean fromCookie) {
        if (fromCookie) {
            return getJwtFromCookie(request);
        }
        return getJwtFromRequest(request);
    }
}
