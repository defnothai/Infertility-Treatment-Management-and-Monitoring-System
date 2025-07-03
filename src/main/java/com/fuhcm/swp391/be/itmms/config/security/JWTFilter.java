package com.fuhcm.swp391.be.itmms.config.security;

import com.fuhcm.swp391.be.itmms.error.exception.AuthenticationException;
import com.fuhcm.swp391.be.itmms.service.JWTService;
import com.fuhcm.swp391.be.itmms.service.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Configuration
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext applicationContext;
    public JWTFilter(){}

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter();
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/auth/register/**",
            "GET:/api/auth/register/**",
            "POST:/api/auth/login",
            "POST:/api/auth/forgot-password",
            "POST:/api/auth/reset-password",
            "GET:/api/home/**",
            "GET:/api/list/**"
    );

    public boolean isPublicAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();

        return PUBLIC_API.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":", 2);
            if (parts.length != 2) return false;

            String allowedMethod = parts[0];
            String allowedUri = parts[1];

            return method.equalsIgnoreCase(allowedMethod) && matcher.match(allowedUri, uri);
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);

        if (token != null && !token.isEmpty()) {
            try {
                String email = jwtService.extractEmail(token);
                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                if (email != null && (currentAuth == null || currentAuth instanceof AnonymousAuthenticationToken)) {
                    MyUserDetailsService userDetailsService = applicationContext.getBean(MyUserDetailsService.class);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    List<GrantedAuthority> authorities = jwtService.getAuthoritiesFromToken(token);
                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                throw new AuthenticationException("Expired token");
            } catch (MalformedJwtException e) {
                throw new AuthenticationException("Invalid token");
            }
        }
        
        if (!isPublicAPI(request.getRequestURI(), request.getMethod()) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new AuthenticationException("Token missing or invalid for protected API");
        }
        filterChain.doFilter(request, response);
    }
}
