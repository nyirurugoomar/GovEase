package com.platform.GovEase.service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.GovEase.dto.user.UserLoginDTO;
import com.platform.GovEase.dto.user.UserResponseDTO;
import com.platform.GovEase.model.user.User;
import com.platform.GovEase.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        // Find user by email or phone number
        User user = null;

        // Try to find by email first
        if (loginDTO.getIdentifier().contains("@")) {
            user = userRepository.findByEmail(loginDTO.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        } else {
            // Try to find by phone number
            user = userRepository.findByPhoneNumber(loginDTO.getIdentifier())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated. Please contact support.");
        }

        // Verify password (TODO: Use BCrypt password encoder)
        if (!user.getPasswordHash().equals(loginDTO.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Update last login timestamp
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Return user response DTO
        return mapToResponseDTO(user);
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
}
