package com.swp391.g1.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.swp391.g1.dto.CurrentUser;
import com.swp391.g1.service.AuthenticationException;
import com.swp391.g1.service.AuthenticationResult;
import com.swp391.g1.util.AuthContext;
import com.swp391.g1.service.IAuthenticationService;
import com.swp391.g1.service.impl.AuthenticationServiceImpl;

/**
 * Login controller. It only orchestrates request/response: credential checks
 * belong to the authentication service and session handling to AuthContext.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IAuthenticationService authenticationService = new AuthenticationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (redirectIfAuthenticated(request, response)) {
            return;
        }
        forwardToLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // An authenticated request must not be able to swap the principal or
        // restart its session; the existing session is left untouched.
        if (redirectIfAuthenticated(request, response)) {
            return;
        }
        String username = request.getParameter("username");

        AuthenticationResult result;
        try {
            result = authenticationService.authenticate(username, request.getParameter("password"));
        } catch (AuthenticationException e) {
            showFailure(request, response, e.getMessage(), username);
            return;
        }
        if (!result.isSuccess()) {
            showFailure(request, response, result.getErrorMessage(), username);
            return;
        }

        AuthContext.createSession(request, result.getCurrentUser());
        response.sendRedirect(contextPath(request) + landingPage(result.getCurrentUser()));
    }

    /**
     * Sends an already-authenticated request to its normal landing page.
     * Returns true when the request was handled, so callers stop processing.
     */
    private boolean redirectIfAuthenticated(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        CurrentUser user = AuthContext.getCurrentUser(request);
        if (user == null) {
            return false;
        }
        response.sendRedirect(contextPath(request) + landingPage(user));
        return true;
    }

    /** Re-renders the form with the generic error; the password is never kept. */
    private void showFailure(HttpServletRequest request, HttpServletResponse response, String errorMessage,
            String username) throws ServletException, IOException {
        request.setAttribute("loginError", errorMessage);
        request.setAttribute("username", username);
        forwardToLogin(request, response);
    }

    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp");
        dispatcher.forward(request, response);
    }

    /** Every role reaches the role-aware home menu after login. */
    private static String landingPage(CurrentUser user) {
        return "/home";
    }

    private static String contextPath(HttpServletRequest request) {
        String ctx = request.getContextPath();
        return ctx == null ? "" : ctx;
    }
}
