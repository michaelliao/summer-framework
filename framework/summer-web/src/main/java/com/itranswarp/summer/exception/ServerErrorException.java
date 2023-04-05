package com.itranswarp.summer.exception;

/**
 * 500 internal server error.
 */
public class ServerErrorException extends ErrorResponseException {

    public ServerErrorException() {
        super(500);
    }

    public ServerErrorException(String message) {
        super(500, message);
    }

    public ServerErrorException(Throwable cause) {
        super(500, cause);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(500, message, cause);
    }
}
