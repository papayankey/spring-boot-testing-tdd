package com.github.papayankey.exceptions.advisor;

import com.github.papayankey.exceptions.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(CustomerNotFoundException exception, HttpServletRequest request) {

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("message", exception.getMessage());
        payload.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        payload.put("path", request.getRequestURI());

        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }

}
