package com.echoItSolution.common_app.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> illegalArgumentException(IllegalArgumentException ex,
                                                                        HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, Object>> handleJwtException(JwtException ex,
                                                                        HttpServletRequest request){
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex,
                                                                  HttpServletRequest request){
        ex.printStackTrace();
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<Map<String, Object>> handleRequestNotPermitted(RequestNotPermitted ex,
                                                                         HttpServletRequest request){
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), request.getServletPath());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex,
                                                               HttpServletRequest request){
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getServletPath());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>(); // Used LinkedHashMap to maintain order
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return new ResponseEntity<>(body, status);
    }

}
