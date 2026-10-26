package com.swp391.g1.service;

/**
 * Wraps unexpected failures with a message that is safe to show to the user;
 * technical details stay in the DAO log.
 */
public class AccountServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountServiceException(String message) {
        super(message);
    }

    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}