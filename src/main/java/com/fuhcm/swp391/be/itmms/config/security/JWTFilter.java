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

    private final JWTService jwtService;
    private final ApplicationContext applicationContext;

    @Autowired
    public JWTFilter(JWTService jwtService,
                     ApplicationContext applicationContext) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring(7);
    }

    private final List<String> PUBLIC_API = List.of(
            "POST:/api/auth/register",
            "POST:/api/auth/login",
            "POST:/api/auth/register/resend-verification-email",
            "POST:/api/auth/forgot-password",
            "POST:/api/auth/reset-password"
    );

    public boolean isPublicAPI(String uri, String method) {
        AntPathMatcher matcher = new AntPathMatcher();

        if(method.equals("GET")) return true;

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

        if (isPublicAPI(request.getRequestURI(), request.getMethod())) {
            filterChain.doFilter(request, response);
        }else {
            String token = getToken(request);
            if(token == null){
                throw new AuthenticationException("Empty token!");
            }

            String email;
            try {
                email = jwtService.extractEmail(token);
            } catch (ExpiredJwtException expiredJwtException) {
                throw new AuthenticationException("Expired Token!");
            } catch (MalformedJwtException malformedJwtException) {
                throw new AuthenticationException("Invalid Token!");
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                MyUserDetailsService userDetailsService = applicationContext.getBean(MyUserDetailsService.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if(jwtService.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        }
    }
}
