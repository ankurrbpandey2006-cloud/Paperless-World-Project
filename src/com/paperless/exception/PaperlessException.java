package com.paperless.exception;

/** Base checked exception for the platform. */
public class PaperlessException extends Exception {
    public PaperlessException(String message) { super(message); }
    public PaperlessException(String message, Throwable cause) { super(message, cause); }
}
