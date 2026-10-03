package com.society.manager.service;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.user.CreateUserRequest;
import com.society.manager.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserDto createUser(CreateUserRequest request);
    UserDto getUserById(UUID id);
    UserDto getUserByUsername(String username);
    PageResponse<UserDto> getAllUsers(Pageable pageable);
    UserDto toggleUserActiveStatus(UUID id, boolean active);
    void deleteUser(UUID id);
}
