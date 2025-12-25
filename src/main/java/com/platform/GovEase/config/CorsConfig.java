package com.platform.GovEase.config;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    
    @Bean
    public CorsFilter corsFilter() {
        
        // Create CORS configuration
        CorsConfiguration config = new CorsConfiguration();
        
 
        config.setAllowedOrigins(Arrays.asList(
   "http://localhost:3000",      // React default port
            "http://localhost:4200",      // Angular default port
            "http://localhost:5173",      // Vite default port
            "http://localhost:8081",      // Alternative backend port
            "http://127.0.0.1:3000"       // Alternative localhost
            
        ));
        
        
        
        
        
        config.setAllowedMethods(Arrays.asList(
            "GET",      // Read data
            "POST",     // Create data
            "PUT",      // Update data
            "PATCH",    // Partial update
            "DELETE",   // Delete data
            "OPTIONS"   // Preflight requests
        ));
        
        
        
        
        
        config.setAllowedHeaders(Arrays.asList(
            "Authorization",     // For JWT tokens
            "Content-Type",      // For JSON, etc.
            "Accept",           // What client accepts
            "X-Requested-With", // AJAX identifier
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // OR - Allow ALL headers
        // config.setAllowedHeaders(Arrays.asList("*"));
        
        
        
        config.setExposedHeaders(Arrays.asList(
            "Authorization",              // So client can read JWT
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        
        
        config.setAllowCredentials(true);
        
        
        
        
        config.setMaxAge(3600L);  // 1 hour
        
        
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // /** means all paths
        
        return new CorsFilter(source);
    }
}
