package com.platform.GovEase.dto.user;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID id;
    private String nationalId;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String fullName;  // Computed field
    private LocalDate dateOfBirth;
    private String gender;
    
    // Address
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
    
    // Profile
    private String profileImageUrl;
    
    // Status
    private Boolean isVerified;
    private Boolean isActive;
    
    // Roles (just the names, not full objects)
    private Set<String> roles;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
