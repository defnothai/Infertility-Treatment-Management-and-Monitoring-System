package com.fuhcm.swp391.be.itmms.config.security;

import com.fuhcm.swp391.be.itmms.service.CustomOAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private UserDetailsService userDetailsService;
    @Autowired
    private JWTFilter jwtFilter;
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    private PasswordEncoder passwordEncoder;


    public SecurityConfig(@Lazy UserDetailsService userDetailsService,
                          JWTFilter jwtFilter,
                          CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                            PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

//    private static final String[] PUBLIC_API = {
//            "/api/auth/register",
//            "/api/auth/login",
//            "/api/auth/register/resend-verification-email",
//            "/api/auth/forgot-password",
//            "/api/auth/reset-password",
//            "/api/auth/register/confirm-email",
//            "/oauth2/**",
//            "/api/home/**",
//            "/api/doctors/**",
//            "/api/services/**",
//            "/api/list/**",
//            "/api/manager/**"
//    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Filter í running");
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                                .requestMatchers("/ws/**").permitAll()
                                .requestMatchers("/topic/**", "/app/**", "/user/**").permitAll()
                                .requestMatchers("/api/application").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register/resend-verification-email").permitAll()
                        .requestMatchers("/api/auth/forgot-password").permitAll()
                        .requestMatchers("/api/auth/reset-password").permitAll()
                        .requestMatchers("/api/auth/register/confirm-email").permitAll()
                        .requestMatchers("/api/home/**").permitAll()
                        .requestMatchers("/api/doctors/**").permitAll()
                        .requestMatchers("/api/services/**").permitAll()
                        .requestMatchers("/api/list/**").permitAll()
                        .requestMatchers("/api/manager/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/consultation").permitAll()
                        .requestMatchers("/o/oauth2/v2/auth/**").permitAll()
                        //.requestMatchers(PUBLIC_API).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payment/vn-pay-callback").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/appointments/confirm-appointment").hasAnyRole("USER")
                        // USER role
                        .requestMatchers(HttpMethod.GET, "/api/user/profile").hasAnyRole("USER", "DOCTOR", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/payment/vn-pay").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/blogs").hasAnyRole("MANAGER", "DOCTOR")
                        .requestMatchers(HttpMethod.GET, "api/user/appointments/available-doctors").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/reviews").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/invoices/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/schedules/view/**").hasAnyRole("USER", "DOCTOR", "MANAGER", "STAFF")

                        // DOCTOR role
                        .requestMatchers(HttpMethod.GET, "/api/blogs/mine").hasRole("DOCTOR")
                        .requestMatchers("/api/blogs/manage/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/patient-records/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/treatments/follow-up/**").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/schedules/manage/**").hasAnyRole("DOCTOR", "ADMIN")

                        //STAFF role
                                .requestMatchers("/api/consultation").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/consultation").hasRole("STAFF")
//                        .requestMatchers(HttpMethod.PUT, "/api/consultation").hasAnyRole("STAFF")
//                        .requestMatchers(HttpMethod.DELETE, "/api/consultation").hasAnyRole("STAFF")

                        //MANAGER role
                        .requestMatchers(HttpMethod.GET, "/api/blogs").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/blogs").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/blogs").hasRole("MANAGER")

                        // ADMIN role
                        .requestMatchers("/api/invoices/**").hasAnyRole("MANAGER", "STAFF")
                        .requestMatchers("/api/reminders/**").hasRole("ADMIN")
                        .requestMatchers("/api/medical-records/authorize/**").hasRole("MANAGER")
                        .requestMatchers("/api/service/manage/**").hasAnyRole("ADMIN", "MANAGER")
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


}
