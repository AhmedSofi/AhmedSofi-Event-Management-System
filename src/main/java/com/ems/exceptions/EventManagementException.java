package com.ems.exceptions;

public class EventManagementException extends RuntimeException {
    public EventManagementException(String message) {
        super(message);
    }

    public EventManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
