package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.user.CreateUserRequest;
import com.society.manager.dto.user.UserDto;
import com.society.manager.entity.Role;
import com.society.manager.entity.User;
import com.society.manager.enums.RoleType;
import com.society.manager.exception.DuplicateResourceException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.RoleRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : request.getRoles()) {
            RoleType roleType = RoleType.valueOf(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName);
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
            roles.add(role);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .active(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        auditLogService.logAction(null, "CREATE_USER", "USER", savedUser.getId().toString(), null, null, "User account created: " + savedUser.getUsername());

        return entityMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return entityMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return entityMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.fromPage(page.map(entityMapper::toUserDto));
    }

    @Override
    @Transactional
    public UserDto toggleUserActiveStatus(UUID id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(active);
        User updated = userRepository.save(user);
        auditLogService.logAction(null, "TOGGLE_USER_STATUS", "USER", id.toString(), null, null, "Active status changed to: " + active);
        return entityMapper.toUserDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        auditLogService.logAction(null, "DELETE_USER", "USER", id.toString(), null, "User deleted: " + user.getUsername(), null);
    }
}
