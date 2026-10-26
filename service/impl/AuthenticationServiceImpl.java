package com.swp391.g1.service.impl;

import java.time.Instant;

import com.swp391.g1.dao.DataAccessException;
import com.swp391.g1.dao.IAccountDAO;
import com.swp391.g1.dao.impl.AccountDAOImpl;
import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.model.Account;
import com.swp391.g1.service.AuthenticationException;
import com.swp391.g1.service.AuthenticationResult;
import com.swp391.g1.service.IAccountService;
import com.swp391.g1.service.IAuthenticationService;
import com.swp391.g1.util.PasswordUtil;

/**
 * Credential verification for login. It owns the "may this account sign in?"
 * decision (password and status) and the user-safe failure contract, and never
 * returns the account row, the password or the stored hash to its caller.
 */
public class AuthenticationServiceImpl implements IAuthenticationService {

    /**
     * One message for unknown username, wrong password and non-active account so
     * that login cannot be used to enumerate accounts.
     */
    private static final String GENERIC_FAILURE = "Tên đăng nhập hoặc mật khẩu không đúng.";

    /**
     * Throwaway credential verified when the username does not exist, so that
     * path costs the same PBKDF2 work as a wrong password and response timing
     * cannot reveal whether a username is registered. It is built once when this
     * class is loaded, not per request, and the verify result is discarded: it
     * can never authenticate an account.
     */
    private static final String DUMMY_CREDENTIAL = PasswordUtil.hash("timing-equalizer-not-a-password");

    private final IAccountDAO accountDAO = new AccountDAOImpl();

    @Override
    public AuthenticationResult authenticate(String username, String password) {
        String normalizedUsername = normalize(username);
        String rawPassword = password == null ? "" : password;
        if (normalizedUsername.isEmpty() || rawPassword.isEmpty()) {
            return AuthenticationResult.failure(GENERIC_FAILURE);
        }

        Account credentials;
        try {
            credentials = accountDAO.findByUsernameWithCredentials(normalizedUsername);
        } catch (DataAccessException e) {
            throw new AuthenticationException("Đăng nhập tạm thời không khả dụng. Vui lòng thử lại sau.", e);
        }
        if (credentials == null) {
            PasswordUtil.verify(rawPassword, DUMMY_CREDENTIAL);
            return AuthenticationResult.failure(GENERIC_FAILURE);
        }
        // Wrong password and non-active status must be indistinguishable, so the
        // password is checked first and both produce the same generic failure.
        if (!PasswordUtil.verify(rawPassword, credentials.getPassword())) {
            return AuthenticationResult.failure(GENERIC_FAILURE);
        }
        if (!IAccountService.STATUS_ACTIVE.equals(credentials.getStatus())) {
            return AuthenticationResult.failure(GENERIC_FAILURE);
        }

        CurrentUser currentUser = new CurrentUser(credentials.getId(), credentials.getUsername(),
                credentials.getType(), credentials.getRole(), Instant.now());
        return AuthenticationResult.success(currentUser);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
