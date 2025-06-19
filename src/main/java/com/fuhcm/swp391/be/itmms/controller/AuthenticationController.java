package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.*;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/test")
    public ResponseEntity test(){
        return ResponseEntity.ok("Test");
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseFormat<Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authenticationService.register(registerRequest);
    }

    @GetMapping("/register/confirm-email")
    public ResponseEntity<ResponseFormat<Object>> confirmEmail(@RequestParam("token") String token) {
        return authenticationService.confirmEmail(token);
    }

    @PostMapping("/register/resend-verification-email")
    public ResponseEntity<ResponseFormat<Object>> resendVerificationEmail(@RequestBody ResendVerificationEmailRequest emailDTO) {
        return authenticationService.resendVerificationEmail(emailDTO.getEmail());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseFormat<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return authenticationService.forgotPassword(forgotPasswordRequest.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseFormat<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest forgotPasswordRequest) {
        return authenticationService.resetPassword(forgotPasswordRequest);
    }

}
