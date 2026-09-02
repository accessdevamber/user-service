package ecommerce.user_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BulkUserRequest(
        @NotEmpty(message = "User list must not be empty")
        List<@Valid UserRequest> users
) {
}
