package br.com.fiap.voltly.advice;

import br.com.fiap.voltly.exception.DuplicateResourceException;
import br.com.fiap.voltly.exception.UserNotFoundException;
import br.com.fiap.voltly.exception.ValidationException;
import br.com.fiap.voltly.utils.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path
    ) {}

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse payload = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(payload);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = ValidationUtil.toErrorMap(ex.getBindingResult());
        return ResponseEntity
                .badRequest()
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse payload = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(payload);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse payload = new ErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(),
                ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(payload);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidation(
            ValidationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ErrorResponse payload = new ErrorResponse(
                Instant.now(), status.value(), status.getReasonPhrase(),
                ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(payload);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String constraint = null;
        if (ex.getCause() instanceof ConstraintViolationException cve) {
            constraint = cve.getConstraintName();
        }
        String message = (constraint != null)
                ? "Unique constraint violated: " + constraint
                : "Database integrity violation";

        ErrorResponse payload = new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }

}
