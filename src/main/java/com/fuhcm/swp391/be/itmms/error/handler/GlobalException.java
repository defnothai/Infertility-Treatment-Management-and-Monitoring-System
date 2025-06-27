package com.fuhcm.swp391.be.itmms.error.handler;

import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.error.exception.AuthenticationException;
import com.fuhcm.swp391.be.itmms.error.exception.ConfirmPasswordNotMatchException;
import com.fuhcm.swp391.be.itmms.error.exception.EmailAlreadyExistsException;
import com.fuhcm.swp391.be.itmms.error.exception.EmailNotFoundException;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseFormat<Object>> handleAllException(Exception e) {
        ResponseFormat<Object> res = new ResponseFormat<Object>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(e.getMessage());
        res.setError("INTERNAL_SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(value = {ConfirmPasswordNotMatchException.class,
                    EmailAlreadyExistsException.class,
        })
    public ResponseEntity<ResponseFormat<Object>> handleUserRequest(Exception e) {
        ResponseFormat<Object> res = new ResponseFormat<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(e.getMessage());
        res.setError("BAD_REQUEST");
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            EmailNotFoundException.class,
            NotFoundException.class,
    })
    public ResponseEntity<ResponseFormat<Object>> handleIdException(Exception ex) {
        ResponseFormat<Object> res = new ResponseFormat<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());;
        res.setMessage(ex.getMessage());
        res.setError("NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseFormat<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        ResponseFormat<Object> res = new ResponseFormat<>(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                errors,
               null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            BadCredentialsException.class,
    })
    public ResponseEntity<ResponseFormat<Object>> handleBadCredentialsException(Exception ex) {
        ResponseFormat<Object> res = new ResponseFormat<Object>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        res.setMessage(ex.getMessage());
        res.setError("BAD_CREDENTIALS");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseFormat<Object>> handleAuthenticationException(AuthenticationException ex) {
        ResponseFormat<Object> res = new ResponseFormat<>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        res.setMessage(ex.getMessage());
        res.setError("UNAUTHORIZED");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }
}
