package com.platform.GovEase.dto.user;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {
    @NotBlank(message = "Email or phone number is required")
    private String identifier; 
    
    @NotBlank(message = "Password is required")
    private String password;
}
