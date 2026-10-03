package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.staff.CreateStaffRequest;
import com.society.manager.dto.staff.StaffDto;
import com.society.manager.entity.Role;
import com.society.manager.entity.Staff;
import com.society.manager.entity.User;
import com.society.manager.enums.RoleType;
import com.society.manager.enums.StaffRole;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.RoleRepository;
import com.society.manager.repository.StaffRepository;
import com.society.manager.repository.UserRepository;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public StaffDto createStaff(CreateStaffRequest request) {
        User user = null;
        if (request.isCreateAccount()) {
            Role staffRole = roleRepository.findByName(request.getRole() == StaffRole.SECURITY ? RoleType.ROLE_SECURITY : RoleType.ROLE_SECURITY)
                    .orElseGet(() -> roleRepository.findByName(RoleType.ROLE_SECURITY).orElseThrow());

            user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "Security@123"))
                    .fullName(request.getFullName())
                    .phoneNumber(request.getPhone())
                    .active(true)
                    .roles(Set.of(staffRole))
                    .build();
            user = userRepository.save(user);
        }

        Staff staff = Staff.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .joiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : java.time.LocalDate.now())
                .salary(request.getSalary())
                .active(true)
                .emergencyContact(request.getEmergencyContact())
                .build();

        Staff saved = staffRepository.save(staff);
        auditLogService.logAction(null, "CREATE_STAFF", "STAFF", saved.getId().toString(), null, null, "Staff created: " + saved.getFullName() + " (" + saved.getRole() + ")");
        return entityMapper.toStaffDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffDto getStaffById(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));
        return entityMapper.toStaffDto(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffDto> getActiveStaff() {
        return staffRepository.findByActiveTrue()
                .stream()
                .map(entityMapper::toStaffDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffDto> getStaffByRole(StaffRole role) {
        return staffRepository.findByRole(role)
                .stream()
                .map(entityMapper::toStaffDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StaffDto> getAllStaff(Pageable pageable) {
        Page<Staff> page = staffRepository.findAll(pageable);
        return PageResponse.fromPage(page.map(entityMapper::toStaffDto));
    }

    @Override
    @Transactional
    public StaffDto toggleStaffActive(UUID id, boolean active) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));
        staff.setActive(active);
        Staff updated = staffRepository.save(staff);
        auditLogService.logAction(null, "TOGGLE_STAFF_STATUS", "STAFF", id.toString(), null, null, "Staff active status changed to: " + active);
        return entityMapper.toStaffDto(updated);
    }

    @Override
    @Transactional
    public void deleteStaff(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + id));
        staffRepository.delete(staff);
        auditLogService.logAction(null, "DELETE_STAFF", "STAFF", id.toString(), null, "Deleted staff: " + staff.getFullName(), null);
    }
}
