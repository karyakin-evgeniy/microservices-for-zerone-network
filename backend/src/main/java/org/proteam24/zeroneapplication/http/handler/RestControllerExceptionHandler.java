package org.proteam24.zeroneapplication.http.handler;

import liquibase.pro.packaged.E;
import org.proteam24.zeroneapplication.dto.ErrorDto;
import org.proteam24.zeroneapplication.security.jwt.JwtAuthenticationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "org.proteam24.zeroneapplication.controller")
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        if (ex instanceof BindException) {
            String bindErrors = ((BindException) ex).getBindingResult().getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(System.lineSeparator()));
            body = new ErrorDto(bindErrors, LocalDateTime.now());
        } else {
            body = new ErrorDto(status.toString(), LocalDateTime.now());
        }

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleAuthException(AuthenticationException authExp) {
        return ResponseEntity.status(401)
                .body(new ErrorDto(authExp.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleJwtException(JwtAuthenticationException jwe) {
        return ResponseEntity.status(403)
                .body(new ErrorDto(jwe.getMessage(), LocalDateTime.now()));
    }
}
