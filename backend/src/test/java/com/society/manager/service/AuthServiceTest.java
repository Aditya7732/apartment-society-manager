package com.society.manager.service;

import com.society.manager.dto.auth.AuthResponse;
import com.society.manager.dto.auth.LoginRequest;
import com.society.manager.entity.Role;
import com.society.manager.entity.User;
import com.society.manager.enums.RoleType;
import com.society.manager.repository.ResidentRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.security.JwtTokenProvider;
import com.society.manager.security.UserPrincipal;
import com.society.manager.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserPrincipal userPrincipal;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        Role role = Role.builder().id(1L).name(RoleType.ROLE_SUPER_ADMIN).build();

        testUser = User.builder()
                .id(userId)
                .username("admin")
                .email("admin@society.com")
                .password("encoded_password")
                .fullName("System Admin")
                .active(true)
                .roles(Set.of(role))
                .build();

        userPrincipal = UserPrincipal.create(testUser);
        authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
    }

    @Test
    @DisplayName("Should successfully authenticate user and return JWT tokens")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("admin");
        request.setPassword("Admin@123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateAccessToken(authentication)).thenReturn("mock_access_token");
        when(tokenProvider.generateRefreshToken("admin")).thenReturn("mock_refresh_token");
        when(residentRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock_access_token", response.getAccessToken());
        assertEquals("mock_refresh_token", response.getRefreshToken());
        assertEquals("admin", response.getUsername());
        assertTrue(response.getRoles().contains("ROLE_SUPER_ADMIN"));

        verify(auditLogService, times(1)).logAction(eq(testUser.getId()), eq("LOGIN"), eq("USER"), anyString(), any(), any(), anyString());
    }
}
