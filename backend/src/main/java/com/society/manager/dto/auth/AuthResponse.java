package com.society.manager.dto.auth;

import java.util.List;
import java.util.UUID;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private UUID userId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;
    private UUID residentId;
    private UUID flatId;
    private String flatNumber;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String tokenType, UUID userId, String username, String email, String fullName, List<String> roles, UUID residentId, UUID flatId, String flatNumber) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        if (tokenType != null) this.tokenType = tokenType;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        this.residentId = residentId;
        this.flatId = flatId;
        this.flatNumber = flatNumber;
    }

    public static AuthResponseBuilder builder() { return new AuthResponseBuilder(); }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public UUID getResidentId() { return residentId; }
    public void setResidentId(UUID residentId) { this.residentId = residentId; }
    public UUID getFlatId() { return flatId; }
    public void setFlatId(UUID flatId) { this.flatId = flatId; }
    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public static class AuthResponseBuilder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private UUID userId;
        private String username;
        private String email;
        private String fullName;
        private List<String> roles;
        private UUID residentId;
        private UUID flatId;
        private String flatNumber;

        public AuthResponseBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public AuthResponseBuilder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public AuthResponseBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseBuilder userId(UUID userId) { this.userId = userId; return this; }
        public AuthResponseBuilder username(String username) { this.username = username; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public AuthResponseBuilder roles(List<String> roles) { this.roles = roles; return this; }
        public AuthResponseBuilder residentId(UUID residentId) { this.residentId = residentId; return this; }
        public AuthResponseBuilder flatId(UUID flatId) { this.flatId = flatId; return this; }
        public AuthResponseBuilder flatNumber(String flatNumber) { this.flatNumber = flatNumber; return this; }

        public AuthResponse build() {
            return new AuthResponse(accessToken, refreshToken, tokenType, userId, username, email, fullName, roles, residentId, flatId, flatNumber);
        }
    }
}
