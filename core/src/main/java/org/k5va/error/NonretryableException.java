package org.k5va.error;

public class NonretryableException extends RuntimeException {

    public NonretryableException(String message) {
        super(message);
    }

    public NonretryableException(Throwable cause) {
        super(cause);
    }
}
