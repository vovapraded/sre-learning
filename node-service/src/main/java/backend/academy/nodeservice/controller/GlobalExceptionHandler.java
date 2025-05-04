package backend.academy.nodeservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WithStatusException.class)
    public ResponseEntity<?> handleWithStatusException(WithStatusException ex) {
        return ResponseEntity
                .status(ex.status())
                .body(Map.of(
                        "error", ex.getMessage(),
                        "status", ex.status().value(),
                        "timestamp", Instant.now().toString()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity
                .internalServerError()
                .body(Map.of(
                        "error", "Internal server error",
                        "details", ex.getMessage(),
                        "timestamp", Instant.now().toString()
                ));
    }
}
