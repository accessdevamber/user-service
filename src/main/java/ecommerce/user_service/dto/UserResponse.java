package ecommerce.user_service.dto;

import ecommerce.user_service.entity.UserRole;
import ecommerce.user_service.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

//only all params constructor present in record
//use userResponse.firstName(), userResponse.role() etc for getters. No "get" keyword here.
//no setter
public record UserResponse(
        //Long id,
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserStatus status,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
