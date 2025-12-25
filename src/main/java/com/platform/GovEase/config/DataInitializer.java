package com.platform.GovEase.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.platform.GovEase.model.user.Role;
import com.platform.GovEase.model.user.Role.RoleName;
import com.platform.GovEase.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
    }

    private void initializeRoles() {
        log.info("Initializing default roles...");

        // Create CITIZEN role if it doesn't exist
        if (!roleRepository.existsByName(RoleName.CITIZEN)) {
            Role citizenRole = Role.builder()
                    .name(RoleName.CITIZEN)
                    .description("Default role for registered citizens")
                    .build();
            roleRepository.save(citizenRole);
            log.info("Created CITIZEN role");
        }

        // Create ORG_STAFF role if it doesn't exist
        if (!roleRepository.existsByName(RoleName.ORG_STAFF)) {
            Role orgStaffRole = Role.builder()
                    .name(RoleName.ORG_STAFF)
                    .description("Role for organization staff members")
                    .build();
            roleRepository.save(orgStaffRole);
            log.info("Created ORG_STAFF role");
        }

        // Create ORG_ADMIN role if it doesn't exist
        if (!roleRepository.existsByName(RoleName.ORG_ADMIN)) {
            Role orgAdminRole = Role.builder()
                    .name(RoleName.ORG_ADMIN)
                    .description("Role for organization administrators")
                    .build();
            roleRepository.save(orgAdminRole);
            log.info("Created ORG_ADMIN role");
        }

        // Create SUPER_ADMIN role if it doesn't exist
        if (!roleRepository.existsByName(RoleName.SUPER_ADMIN)) {
            Role superAdminRole = Role.builder()
                    .name(RoleName.SUPER_ADMIN)
                    .description("Role for system administrators with full access")
                    .build();
            roleRepository.save(superAdminRole);
            log.info("Created SUPER_ADMIN role");
        }

        log.info("Role initialization completed");
    }
}
