package com.mini.bank.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiError {

    private String message;
    private int status;
    private String error;
    private String path;

    @JsonFormat(shape =  JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(String message, int status, String error, String path) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

}
