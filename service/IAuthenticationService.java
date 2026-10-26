package com.swp391.g1.service;

public interface IAuthenticationService {
    AuthenticationResult authenticate(String username, String password);
}
