package ecommerce.user_service.dto;

import java.util.UUID;

public record FirstNameCursor(
        String firstName,
        //Long id
        UUID id
) {
}
