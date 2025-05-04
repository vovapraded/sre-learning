package backend.academy.nodeservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WithStatusException extends ApplicationException {
    private final HttpStatus status;

    public WithStatusException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public WithStatusException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
