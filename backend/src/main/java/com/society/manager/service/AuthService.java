package com.society.manager.service;

import com.society.manager.dto.auth.AuthResponse;
import com.society.manager.dto.auth.ChangePasswordRequest;
import com.society.manager.dto.auth.LoginRequest;
import com.society.manager.dto.auth.RefreshTokenRequest;

import java.util.UUID;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void changePassword(UUID userId, ChangePasswordRequest request);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
