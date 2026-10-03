package com.society.manager.service.impl;

import com.society.manager.dto.auth.AuthResponse;
import com.society.manager.dto.auth.ChangePasswordRequest;
import com.society.manager.dto.auth.LoginRequest;
import com.society.manager.dto.auth.RefreshTokenRequest;
import com.society.manager.entity.Resident;
import com.society.manager.entity.User;
import com.society.manager.exception.BadRequestException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.security.JwtTokenProvider;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ResidentRepository residentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(userPrincipal.getUsername());

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Find resident details if applicable
        UUID residentId = null;
        UUID flatId = null;
        String flatNumber = null;

        Optional<Resident> residentOpt = residentRepository.findByUserId(userPrincipal.getId());
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            residentId = resident.getId();
            if (resident.getFlat() != null) {
                flatId = resident.getFlat().getId();
                flatNumber = resident.getFlat().getFlatNumber();
            }
        }

        auditLogService.logAction(userPrincipal.getId(), "LOGIN", "USER", userPrincipal.getId().toString(), null, null, "User logged in successfully");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .roles(roles)
                .residentId(residentId)
                .flatId(flatId)
                .flatNumber(flatNumber)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = tokenProvider.getUsernameFromJWT(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UUID residentId = null;
        UUID flatId = null;
        String flatNumber = null;

        Optional<Resident> residentOpt = residentRepository.findByUserId(user.getId());
        if (residentOpt.isPresent()) {
            Resident resident = residentOpt.get();
            residentId = resident.getId();
            if (resident.getFlat() != null) {
                flatId = resident.getFlat().getId();
                flatNumber = resident.getFlat().getFlatNumber();
            }
        }

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .residentId(residentId)
                .flatId(flatId)
                .flatNumber(flatNumber)
                .build();
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.logAction(userId, "CHANGE_PASSWORD", "USER", userId.toString(), null, null, "Password changed successfully");
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user account registered with email: " + email));
        log.info("Password reset requested for email: {}", email);
        // Abstraction for sending email token in future
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Implementation placeholder for token-based password reset
        log.info("Password reset executed with token: {}", token);
    }
}
