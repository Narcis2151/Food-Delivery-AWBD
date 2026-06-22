package com.unibuc.apigateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Map;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    );

    private final Key signInKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signInKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || isPublic(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(response, "Missing or malformed Authorization header");
            return;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signInKey)
                    .build()
                    .parseClaimsJws(authHeader.substring(7))
                    .getBody();

            HttpServletRequest mutated = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("X-Auth-User".equalsIgnoreCase(name)) {
                        return claims.getSubject();
                    }
                    return super.getHeader(name);
                }
            };
            filterChain.doFilter(mutated, response);
        } catch (Exception e) {
            unauthorized(response, "Invalid or expired token");
        }
    }

    private boolean isPublic(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, path));
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), Map.of("message", message));
    }
}
