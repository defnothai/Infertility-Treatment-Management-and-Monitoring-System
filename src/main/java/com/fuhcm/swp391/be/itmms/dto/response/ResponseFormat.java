package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseFormat<T> {
    private int statusCode;
    private String error;
    private Object message;
    private T data;
}
