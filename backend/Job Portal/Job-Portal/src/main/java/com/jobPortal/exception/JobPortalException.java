package com.jobPortal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JobPortalException extends Exception {
    private final HttpStatus status;

    public JobPortalException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
