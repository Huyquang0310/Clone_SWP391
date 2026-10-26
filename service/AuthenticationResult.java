package com.swp391.g1.service;

import com.swp391.g1.dto.CurrentUser;

/**
 * Outcome of a login attempt. It carries only the authenticated principal or a
 * user-safe message, so the password and its hash cannot leave the service.
 */
public class AuthenticationResult {

    private final boolean success;
    private final CurrentUser currentUser;
    private final String errorMessage;

    private AuthenticationResult(boolean success, CurrentUser currentUser, String errorMessage) {
        this.success = success;
        this.currentUser = currentUser;
        this.errorMessage = errorMessage;
    }

    public static AuthenticationResult success(CurrentUser currentUser) {
        return new AuthenticationResult(true, currentUser, null);
    }

    public static AuthenticationResult failure(String errorMessage) {
        return new AuthenticationResult(false, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
