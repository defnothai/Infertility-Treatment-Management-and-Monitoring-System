package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.config.ModelMapperConfig;
import com.fuhcm.swp391.be.itmms.constant.AccountRole;
import com.fuhcm.swp391.be.itmms.constant.AccountStatus;
import com.fuhcm.swp391.be.itmms.dto.request.LoginRequest;
import com.fuhcm.swp391.be.itmms.dto.request.RegisterRequest;
import com.fuhcm.swp391.be.itmms.dto.request.ResetPasswordRequest;
import com.fuhcm.swp391.be.itmms.dto.response.EmailDetail;
import com.fuhcm.swp391.be.itmms.dto.response.LoginResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.error.exception.ConfirmPasswordNotMatchException;
import com.fuhcm.swp391.be.itmms.error.exception.EmailAlreadyExistsException;
import com.fuhcm.swp391.be.itmms.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthenticationService {

    private final AuthenticationRepository authenticationRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private static final String BASE_URL = "http://localhost:3000/xac-nhan-email";
    private static final String FORGOT_PASSWORD_URL = "http://localhost:3000/cap-nhat-mat-khau";
    private final ConfirmTokenRegisterService confirmTokenRegisterService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public AuthenticationService(AuthenticationRepository authenticationRepository,
                                 JWTService jwtService,
                                 AuthenticationManager authenticationManager,
                                 ModelMapperConfig modelMapperConfig,
                                 EmailService emailService,
                                 @Lazy ConfirmTokenRegisterService confirmTokenRegisterService,
                                 PasswordEncoder passwordEncoder,
                                 RoleService roleService) {
        this.authenticationRepository = authenticationRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapperConfig.modelMapper();
        this.emailService = emailService;
        this.confirmTokenRegisterService = confirmTokenRegisterService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    public Account getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return this.findByEmail(email);
        }
        return null;
    }

    public Account findByEmail(String email) {
        return authenticationRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));
    }

    public boolean existsByGmail(String gmail) {
        return authenticationRepository.existsByEmail(gmail);
    }

    public void enableAccount(String email) {
        this.findByEmail(email).setStatus(AccountStatus.ENABLED);
    }


    public boolean isValidConfirmPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    public AccountStatus getStatusByEmail(String email) {
        return this.findByEmail(email).getStatus();
    }


    public ResponseEntity<ResponseFormat<Object>> login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
        }catch (Exception e){
            throw new BadCredentialsException("Thông tin đăng nhập không chính xác");
        }

        Account account = this.findByEmail(loginRequest.getEmail());
        LoginResponse loginResponse = modelMapper.map(account, LoginResponse.class);
        String token = jwtService.generateJWT(account.getEmail());
        loginResponse.setToken(token);
        return ResponseEntity.ok
                (new ResponseFormat<>(HttpStatus.OK.value(),
                        "LOGIN_SUCCESS", "Đăng nhập thành công", loginResponse));
    }

    @Transactional
    public ResponseEntity<ResponseFormat<Object>> register(RegisterRequest registerRequest) {
        if (!isValidConfirmPassword(
                registerRequest.getPassword(),
                registerRequest.getConfirmPassword())) {
            throw new ConfirmPasswordNotMatchException("Xác nhận mật khẩu không khớp mật khẩu đã nhập");
        }

        String email = registerRequest.getEmail();
        Account account = new Account();
        LocalDateTime now = LocalDateTime.now();
        if (existsByGmail(email)) {
            if (this.getStatusByEmail(email) != AccountStatus.DISABLED) {
                throw new EmailAlreadyExistsException("Email đã tồn tại");
            }else {
                account = this.findByEmail(email);
                account.setFullName(registerRequest.getFullName());
                account.setEmail(email);
                account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                account.setCreatedAt(now);
                account.setStatus(AccountStatus.DISABLED);
                account.setCreatedBy(account);
                account = authenticationRepository.save(account);
            }
        }else {
            account.setFullName(registerRequest.getFullName());
            account.setEmail(email);
            account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            account.setCreatedAt(now);
            account.setStatus(AccountStatus.DISABLED);
            account.setCreatedBy(account);
            account.setRoles(List.of(roleService.findByRoleName(AccountRole.ROLE_USER)));
            authenticationRepository.save(account);
        }

        String token = confirmTokenRegisterService.generateToken(account,now);

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(registerRequest.getEmail());
        emailDetail.setSubject("XÁC NHẬN TÀI KHOẢN NGƯỜI DÙNG");
        emailDetail.setFullName(registerRequest.getFullName());
        emailDetail.setLink(BASE_URL + "?token=" + token);
        emailService.sendRegistrationEmail(emailDetail);

        return ResponseEntity.ok(
                new ResponseFormat<>(200, "OK",
                        "Email xác nhận đã được gửi, vui lòng kiểm tra hộp thư", null));
    }

    public ResponseEntity<ResponseFormat<Object>> confirmEmail(String token) {

        Account account = confirmTokenRegisterService.extractAccount(token);

        if (this.getStatusByEmail(account
                .getEmail()) != AccountStatus.DISABLED) {
            throw new IllegalArgumentException("Email đã xác thực");
        }

        if (!confirmTokenRegisterService.isValidVersionToken(token)) {
            throw new RuntimeException("Thông tin xác nhận không hợp lệ");
        }

        if (confirmTokenRegisterService.isTokenExpired(token)) {
            throw new RuntimeException("Đã hết hạn xác thực. Vui lòng yêu cầu gửi lại mail");
        }

        account.setStatus(AccountStatus.ENABLED);
        account.setCreatedAt(LocalDateTime.now());
        Account updatedAccount = authenticationRepository.save(account);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.CREATED.value(), "ACCOUNT_CREATED", "Tạo tài khoản thành công", null));
    }


    @Transactional
    public ResponseEntity<ResponseFormat<Object>> resendVerificationEmail(String email) {
        Account account = this.findByEmail(email);

        if (account.getStatus() != AccountStatus.DISABLED) {
            throw new IllegalArgumentException("Email đã được xác thực");
        }

        LocalDateTime now = LocalDateTime.now();
        String newToken = confirmTokenRegisterService.generateToken(account, now);

        account.setCreatedAt(now);
        authenticationRepository.save(account);

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(account.getEmail());
        emailDetail.setSubject("XÁC NHẬN LẠI TÀI KHOẢN NGƯỜI DÙNG");
        emailDetail.setFullName(account.getFullName());
        emailDetail.setLink(BASE_URL + "?token=" + newToken);

        emailService.sendRegistrationEmail(emailDetail);

        return ResponseEntity.ok(new ResponseFormat<>(200, "OK", "Email xác nhận đã được gửi lại, vui lòng kiểm tra hộp thư", null));
    }

    public ResponseEntity<ResponseFormat<Object>> forgotPassword(String email) {
        Account account = this.findByEmail(email);
        String token = jwtService.generateJWT(account.getEmail());
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(account.getEmail());
        emailDetail.setSubject("ĐẶT LẠI MẬT KHẨU");
        emailDetail.setFullName(account.getFullName());
        emailDetail.setLink(FORGOT_PASSWORD_URL + "?token=" + token);
        emailService.sendForgotPasswordEmail(emailDetail);
        return ResponseEntity.ok(
                new ResponseFormat<>(200, "OK",
                        "Email xác nhận đã được gửi, vui lòng kiểm tra hộp thư", token));
    }

    public ResponseEntity<ResponseFormat<Object>> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        if (!isValidConfirmPassword(resetPasswordRequest.getPassword(), resetPasswordRequest.getConfirmPassword())) {
            throw new ConfirmPasswordNotMatchException("Xác nhận mật khẩu không khớp mật khẩu");
        }
        String token = resetPasswordRequest.getToken();
        Account account = jwtService.extractAccount(token);
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        authenticationRepository.save(account);
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(), "RESET_PASSWORD", "Đặt lại mật khẩu thành công", null));
    }
}
