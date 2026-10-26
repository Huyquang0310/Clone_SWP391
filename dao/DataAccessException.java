package com.swp391.g1.dao;

/**
 * Wraps SQL failures from the DAO layer. duplicateKey marks a
 * unique-constraint violation such as a duplicate username.
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final boolean duplicateKey;

    public DataAccessException(String message) {
        super(message);
        this.duplicateKey = false;
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        this.duplicateKey = false;
    }

    public DataAccessException(String message, Throwable cause, boolean duplicateKey) {
        super(message, cause);
        this.duplicateKey = duplicateKey;
    }

    public boolean isDuplicateKey() {
        return duplicateKey;
    }
}