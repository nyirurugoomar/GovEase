package com.platform.GovEase.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.GovEase.dto.user.UserRegistrationDTO;
import com.platform.GovEase.dto.user.UserResponseDTO;
import com.platform.GovEase.dto.user.UserUpdateDTO;
import com.platform.GovEase.model.user.Role;
import com.platform.GovEase.model.user.Role.RoleName;
import com.platform.GovEase.model.user.User;
import com.platform.GovEase.repository.RoleRepository;
import com.platform.GovEase.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {

        // Check if user already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByNationalId(registrationDTO.getNationalId())) {
            throw new RuntimeException("National ID already exists");
        }
        if (userRepository.existsByPhoneNumber(registrationDTO.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Create user entity
        User user = User.builder()
                .nationalId(registrationDTO.getNationalId())
                .email(registrationDTO.getEmail())
                .phoneNumber(registrationDTO.getPhoneNumber())
                .passwordHash(registrationDTO.getPassword()) // TODO: Hash password with BCrypt
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .dateOfBirth(registrationDTO.getDateOfBirth())
                .gender(registrationDTO.getGender() != null ? User.Gender.valueOf(registrationDTO.getGender()) : null)
                .address(buildAddress(registrationDTO))
                .isVerified(false)
                .isActive(true)
                .build();

        // Assign default CITIZEN role
        Role citizenRole = roleRepository.findByName(RoleName.CITIZEN)
                .orElseThrow(() -> new RuntimeException("CITIZEN role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(citizenRole);
        user.setRoles(roles);

        // Save user
        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update fields if provided
        if (updateDTO.getPhoneNumber() != null && !updateDTO.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(updateDTO.getPhoneNumber())) {
                throw new RuntimeException("Phone number already exists");
            }
            user.setPhoneNumber(updateDTO.getPhoneNumber());
        }

        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }

        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }

        if (updateDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(updateDTO.getDateOfBirth());
        }

        if (updateDTO.getGender() != null) {
            user.setGender(User.Gender.valueOf(updateDTO.getGender()));
        }

        if (updateDTO.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDTO.getProfileImageUrl());
        }

        // Build and update address from location fields
        String updatedAddress = buildAddressFromUpdate(updateDTO);
        if (updatedAddress != null) {
            user.setAddress(updatedAddress);
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        // Parse address field if it exists
        String[] addressParts = parseAddress(user.getAddress());

        return UserResponseDTO.builder()
                .id(user.getId())
                .nationalId(user.getNationalId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .province(addressParts[0])
                .district(addressParts[1])
                .sector(addressParts[2])
                .cell(addressParts[3])
                .village(addressParts[4])
                .profileImageUrl(user.getProfileImageUrl())
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    private String[] parseAddress(String address) {
        String[] parts = new String[5]; // [province, district, sector, cell, village]

        if (address == null || address.isEmpty()) {
            return parts;
        }

        String[] splitAddress = address.split(", ");
        for (int i = 0; i < splitAddress.length && i < 5; i++) {
            parts[i] = splitAddress[i].trim();
        }

        return parts;
    }

    private String buildAddress(UserRegistrationDTO dto) {
        StringBuilder address = new StringBuilder();
        if (dto.getProvince() != null) address.append(dto.getProvince()).append(", ");
        if (dto.getDistrict() != null) address.append(dto.getDistrict()).append(", ");
        if (dto.getSector() != null) address.append(dto.getSector()).append(", ");
        if (dto.getCell() != null) address.append(dto.getCell()).append(", ");
        if (dto.getVillage() != null) address.append(dto.getVillage());

        String result = address.toString();
        return result.isEmpty() ? null : result.replaceAll(", $", "");
    }

    private String buildAddressFromUpdate(UserUpdateDTO dto) {
        StringBuilder address = new StringBuilder();
        if (dto.getProvince() != null) address.append(dto.getProvince()).append(", ");
        if (dto.getDistrict() != null) address.append(dto.getDistrict()).append(", ");
        if (dto.getSector() != null) address.append(dto.getSector()).append(", ");
        if (dto.getCell() != null) address.append(dto.getCell()).append(", ");
        if (dto.getVillage() != null) address.append(dto.getVillage());

        String result = address.toString();
        return result.isEmpty() ? null : result.replaceAll(", $", "");
    }
}
