package backend.academy.nodeservice.exception;

import org.springframework.http.HttpStatus;

public class KeyNotFoundException extends WithStatusException {
    public KeyNotFoundException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }

    public KeyNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
