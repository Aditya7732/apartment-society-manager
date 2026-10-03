package com.society.manager.security;

import com.society.manager.entity.Resident;
import com.society.manager.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final ResidentRepository residentRepository;

    public Optional<UserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId).orElse(null);
    }

    public boolean hasRole(String role) {
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return getCurrentUser()
                .map(UserPrincipal::getAuthorities)
                .map(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(a -> a.equals(roleWithPrefix)))
                .orElse(false);
    }

    public boolean isResident() {
        return hasRole("ROLE_RESIDENT") && !hasRole("ROLE_SUPER_ADMIN") && !hasRole("ROLE_SOCIETY_ADMIN");
    }

    public boolean isAdminOrManager() {
        return hasRole("ROLE_SUPER_ADMIN") || hasRole("ROLE_SOCIETY_ADMIN");
    }

    public boolean isAccountant() {
        return hasRole("ROLE_ACCOUNTANT");
    }

    public boolean isSecurity() {
        return hasRole("ROLE_SECURITY");
    }

    public Optional<Resident> getCurrentResident() {
        UUID userId = getCurrentUserId();
        if (userId == null) {
            return Optional.empty();
        }
        return residentRepository.findByUserId(userId);
    }
}
