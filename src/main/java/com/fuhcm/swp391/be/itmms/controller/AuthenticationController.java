package com.fuhcm.swp391.be.itmms.controller;

import com.fuhcm.swp391.be.itmms.dto.request.*;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(value = "localhost:3000")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok
                (new ResponseFormat<>(HttpStatus.OK.value(),
                                "LOGIN_SUCCESS",
                            "Đăng nhập thành công",
                                    authenticationService.login(loginRequest)));
    }

    @PostMapping("/test")
    public ResponseEntity test(){
        return ResponseEntity.ok("Test");
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseFormat<Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return ResponseEntity.ok(
                new ResponseFormat<>(200,
                                "EMAIL_VERIFICATION_SENT",
                            "Email xác nhận đã được gửi, vui lòng kiểm tra hộp thư",
                                    null));

    }

    @GetMapping("/register/confirm-email")
    public ResponseEntity<ResponseFormat<Object>> confirmEmail(@RequestParam("token") String token) {
        authenticationService.confirmEmail(token);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.CREATED.value(),
                                    "ACCOUNT_CREATED",
                                            "Tạo tài khoản thành công", null));
    }

    @PostMapping("/register/resend-verification-email")
    public ResponseEntity<ResponseFormat<Object>> resendVerificationEmail(@RequestBody ResendVerificationEmailRequest emailDTO) {
        authenticationService.resendVerificationEmail(emailDTO.getEmail());
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                                "EMAIL_RESENT",
                                        "Email xác nhận đã được gửi lại, vui lòng kiểm tra hộp thư",
                                                    null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseFormat<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.forgotPassword(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok(
                new ResponseFormat<>(200, "OK",
                        "Email xác nhận đã được gửi, vui lòng kiểm tra hộp thư", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseFormat<Object>> resetPassword(@Valid @RequestBody ResetPasswordRequest forgotPasswordRequest) {
        authenticationService.resetPassword(forgotPasswordRequest);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                                "RESET_PASSWORD_SUCCESS",
                        "Đặt lại mật khẩu thành công", null));

    }

}
