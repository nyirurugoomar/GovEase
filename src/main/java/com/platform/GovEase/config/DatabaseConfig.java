package com.platform.GovEase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {

    // JPA Auditing enables automatic population of @CreatedDate, @LastModifiedDate, etc.
    // Transaction Management ensures proper database transaction handling
}
