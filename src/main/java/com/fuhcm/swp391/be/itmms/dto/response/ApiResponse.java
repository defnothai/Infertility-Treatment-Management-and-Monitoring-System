package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Setter
@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    private T data;


    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.statusCode = HttpStatus.OK.value();
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.data = data;
    }
}
