package ecommerce.user_service.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email,
        String phone
) {}
