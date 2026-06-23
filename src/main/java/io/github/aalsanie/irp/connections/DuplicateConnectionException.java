package io.github.aalsanie.irp.connections;

public class DuplicateConnectionException extends RuntimeException {

    public DuplicateConnectionException(String message) {
        super(message);
    }

    public DuplicateConnectionException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

}
