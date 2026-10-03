package com.society.manager.service.impl;

import com.society.manager.dto.PageResponse;
import com.society.manager.dto.resident.*;
import com.society.manager.entity.*;
import com.society.manager.enums.OccupancyStatus;
import com.society.manager.enums.RoleType;
import com.society.manager.exception.BadRequestException;
import com.society.manager.exception.DuplicateResourceException;
import com.society.manager.exception.ResourceNotFoundException;
import com.society.manager.mapper.EntityMapper;
import com.society.manager.repository.*;
import com.society.manager.service.AuditLogService;
import com.society.manager.service.ResidentService;
import com.society.manager.specification.ResidentSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResidentServiceImpl implements ResidentService {

    private final ResidentRepository residentRepository;
    private final FlatRepository flatRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityMapper entityMapper;
    private final AuditLogService auditLogService;
    private final com.society.manager.security.SecurityUtils securityUtils;

    @Override
    @Transactional
    public ResidentDto createResident(CreateResidentRequest request) {
        Flat flat = flatRepository.findById(request.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + request.getFlatId()));

        if (residentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Resident with email '" + request.getEmail() + "' already exists");
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User account not found"));
        } else if (request.isCreateAccount()) {
            // Auto create resident user account with username derived from email or name
            String baseUsername = request.getEmail().split("@")[0];
            String username = baseUsername;
            int counter = 1;
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + counter++;
            }

            Role residentRole = roleRepository.findByName(RoleType.ROLE_RESIDENT)
                    .orElseThrow(() -> new ResourceNotFoundException("ROLE_RESIDENT not found"));

            user = User.builder()
                    .username(username)
                    .email(request.getEmail())
                    .password(passwordEncoder.encode("Password@123")) // Default password for new resident account
                    .fullName(request.getFirstName() + " " + request.getLastName())
                    .phoneNumber(request.getPhone())
                    .active(true)
                    .roles(Set.of(residentRole))
                    .build();
            user = userRepository.save(user);
        }

        Resident resident = Resident.builder()
                .user(user)
                .flat(flat)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .moveInDate(request.getMoveInDate() != null ? request.getMoveInDate() : LocalDate.now())
                .isOwner(request.isOwner())
                .active(true)
                .build();

        Resident saved = residentRepository.save(resident);

        // Update flat occupancy status
        flat.setOccupancyStatus(request.isOwner() ? OccupancyStatus.OWNER_OCCUPIED : OccupancyStatus.TENANT_OCCUPIED);
        flatRepository.save(flat);

        auditLogService.logAction(null, "CREATE_RESIDENT", "RESIDENT", saved.getId().toString(), null, null, "Resident created: " + saved.getFullName() + " for flat " + flat.getFlatNumber());
        return entityMapper.toResidentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentDto getResidentById(UUID id) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + id));

        if (securityUtils.isResident()) {
            Resident currentResident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            if (!resident.getId().equals(currentResident.getId())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another resident's private profile.");
            }
        }

        return entityMapper.toResidentDto(resident);
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentDto getResidentByUserId(UUID userId) {
        Resident resident = residentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident profile not found for user id: " + userId));

        if (securityUtils.isResident()) {
            UUID currentUserId = securityUtils.getCurrentUserId();
            if (!userId.equals(currentUserId)) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view another user's resident profile.");
            }
        }

        return entityMapper.toResidentDto(resident);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ResidentDto> searchResidents(UUID buildingId, Boolean active, Boolean isOwner, String search, Pageable pageable) {
        if (securityUtils.isResident()) {
            Resident resident = securityUtils.getCurrentResident()
                    .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Resident profile not found"));
            return PageResponse.fromPage(new org.springframework.data.domain.PageImpl<>(
                    List.of(entityMapper.toResidentDto(resident)),
                    pageable,
                    1
            ));
        }

        Specification<Resident> spec = ResidentSpecification.filterResidents(buildingId, active, isOwner, search);
        Page<Resident> page = residentRepository.findAll(spec, pageable);
        return PageResponse.fromPage(page.map(entityMapper::toResidentDto));
    }

    @Override
    @Transactional
    public ResidentDto updateResident(UUID id, CreateResidentRequest request) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + id));

        resident.setFirstName(request.getFirstName());
        resident.setLastName(request.getLastName());
        resident.setEmail(request.getEmail());
        resident.setPhone(request.getPhone());
        resident.setEmergencyContactName(request.getEmergencyContactName());
        resident.setEmergencyContactPhone(request.getEmergencyContactPhone());
        resident.setOwner(request.isOwner());
        if (request.getMoveInDate() != null) {
            resident.setMoveInDate(request.getMoveInDate());
        }

        Resident updated = residentRepository.save(resident);
        auditLogService.logAction(null, "UPDATE_RESIDENT", "RESIDENT", id.toString(), null, null, "Resident updated: " + updated.getFullName());
        return entityMapper.toResidentDto(updated);
    }

    @Override
    @Transactional
    public ResidentDto deactivateResident(UUID id) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + id));

        resident.setActive(false);
        resident.setMoveOutDate(LocalDate.now());

        if (resident.getUser() != null) {
            resident.getUser().setActive(false);
            userRepository.save(resident.getUser());
        }

        Flat flat = resident.getFlat();
        if (flat != null) {
            flat.setOccupancyStatus(OccupancyStatus.VACANT);
            flatRepository.save(flat);
        }

        Resident updated = residentRepository.save(resident);
        auditLogService.logAction(null, "DEACTIVATE_RESIDENT", "RESIDENT", id.toString(), null, null, "Resident deactivated: " + updated.getFullName());
        return entityMapper.toResidentDto(updated);
    }

    @Override
    @Transactional
    public FamilyMemberDto addFamilyMember(UUID residentId, FamilyMemberDto dto) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + residentId));

        FamilyMember fm = FamilyMember.builder()
                .resident(resident)
                .fullName(dto.getFullName())
                .relation(dto.getRelation())
                .age(dto.getAge())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        FamilyMember saved = familyMemberRepository.save(fm);
        return entityMapper.toFamilyMemberDto(saved);
    }

    @Override
    @Transactional
    public void removeFamilyMember(UUID familyMemberId) {
        FamilyMember fm = familyMemberRepository.findById(familyMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Family member not found"));
        familyMemberRepository.delete(fm);
    }

    @Override
    @Transactional
    public VehicleDto addVehicle(UUID residentId, VehicleDto dto) {
        Resident resident = residentRepository.findById(residentId)
                .orElseThrow(() -> new ResourceNotFoundException("Resident not found with id: " + residentId));

        if (vehicleRepository.existsByVehicleNumber(dto.getVehicleNumber())) {
            throw new DuplicateResourceException("Vehicle number '" + dto.getVehicleNumber() + "' is already registered");
        }

        Vehicle vehicle = Vehicle.builder()
                .resident(resident)
                .flat(resident.getFlat())
                .vehicleNumber(dto.getVehicleNumber())
                .vehicleType(dto.getVehicleType())
                .parkingSlot(dto.getParkingSlot() != null ? dto.getParkingSlot() : resident.getFlat().getParkingSlot())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return entityMapper.toVehicleDto(saved);
    }

    @Override
    @Transactional
    public void removeVehicle(UUID vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        vehicleRepository.delete(vehicle);
    }
}
