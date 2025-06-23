package com.fuhcm.swp391.be.itmms.config.security;

import com.fuhcm.swp391.be.itmms.service.CustomOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JWTFilter jwtFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final PasswordEncoder passwordEncoder;


    public SecurityConfig(@Lazy UserDetailsService userDetailsService,
                          JWTFilter jwtFilter,
                          CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    private static final String[] PUBLIC_API = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/register/resend-verification-email",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/register/confirm-email",
            "/oauth2/**",
            "/oauth2/authorization/**",
            "/api/home/**",
            "/api/doctors/**",
            "/api/services/**",
            "/api/blogs/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(PUBLIC_API).permitAll()

                        // USER role
                        .requestMatchers(HttpMethod.GET, "/api/user/profile").hasAnyRole("USER", "DOCTOR", "MANAGER", "STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/blogs").hasAnyRole("MANAGER", "DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/invoices/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/schedules/view/**").hasAnyRole("USER", "DOCTOR", "MANAGER", "STAFF")

                        // DOCTOR role
                        .requestMatchers("/api/blogs/manage/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/patient-records/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/treatments/follow-up/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/schedules/manage/**").hasAnyRole("DOCTOR", "ADMIN")

                        // ADMIN role
                        .requestMatchers("/api/invoices/**").hasAnyRole("MANAGER", "STAFF")
                        .requestMatchers("/api/reminders/**").hasRole("ADMIN")
                        .requestMatchers("/api/medical-records/authorize/**").hasRole("MANAGER")
                        .requestMatchers("/api/services/manage/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/reviews/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/accounts/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")

                        // Mặc định các request khác đều cần authentication
                        .anyRequest().authenticated()
                ).oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
