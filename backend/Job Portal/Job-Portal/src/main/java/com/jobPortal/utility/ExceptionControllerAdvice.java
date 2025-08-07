package com.jobPortal.utility;

import com.jobPortal.exception.JobPortalException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @Autowired
    private Environment environment;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> generalException(Exception exception){

        ErrorInfo errorInfo = new ErrorInfo(
                "Something went wrong: " + exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now());
        return new ResponseEntity<>(errorInfo,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorInfo> validatorExceptionHandler(Exception exception) {
        String message = "";
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            message = methodArgumentNotValidException.getAllErrors()
                    .stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(","));
        } else {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
            message = constraintViolationException.getConstraintViolations()
                    .stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
        }

        ErrorInfo errorInfo = new ErrorInfo(
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobPortalException.class)
    public ResponseEntity<ErrorInfo> handleJobPortalException(JobPortalException exception){
        String message = environment.getProperty(exception.getMessage(), exception.getMessage());
        ErrorInfo errorInfo = new ErrorInfo(
                message,
                exception.getStatus().value(),
                LocalDateTime.now());
        return new ResponseEntity<>(errorInfo, exception.getStatus());
    }

}
