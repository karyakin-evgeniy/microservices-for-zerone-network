package org.proteam24.zeroneapplication.http.handler;

import org.proteam24.zeroneapplication.dto.ErrorDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
}
