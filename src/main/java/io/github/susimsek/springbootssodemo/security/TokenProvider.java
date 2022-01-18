package io.github.susimsek.springbootssodemo.security;

import io.github.susimsek.springbootssodemo.dto.Token;
import io.github.susimsek.springbootssodemo.dto.TokenType;
import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class TokenProvider {

    final AuthProperties authProperties;


    public String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(authProperties.getTokenSecret())
                .parseClaimsJws(token).getBody()
                .getSubject();
    }

    public String resolveCookieToken(String tokenCookieName, HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName() != null && cookie.getName().equals(tokenCookieName))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(authProperties.getTokenSecret()).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            System.out.println("IIIIIIInvalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT exception");
        } catch (IllegalArgumentException ex) {
            System.out.println("Jwt claims string is empty");
        }
        return false;
    } 

    public Token generateToken(UserPrincipal user) {
        return getToken(TokenType.ACCESS, user, authProperties.getTokenExpirationMs());
    }

    public Token generateRefreshToken(UserPrincipal user) {
        return getToken(TokenType.REFRESH, user, authProperties.getRefreshTokenExpirationMs());
    }

    public Token getToken(TokenType type, UserPrincipal user, Long expirationMs) {
        Claims claims = Jwts.claims();
        Date now = new Date();
        long duration = now.getTime() + expirationMs;
        Date expiryDate = new Date(duration);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.id()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, authProperties.getTokenSecret()).compact();
        return new Token(type, token,
                duration, LocalDateTime.ofInstant(expiryDate.toInstant(),
                ZoneId.systemDefault()));
   }
}
