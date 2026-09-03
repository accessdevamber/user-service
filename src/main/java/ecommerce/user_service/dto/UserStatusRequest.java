package ecommerce.user_service.dto;

import ecommerce.user_service.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(

        @NotNull(message = "Status is required")
        UserStatus status
) {
}
