package ecommerce.user_service.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        ErrorCode errorCode,
        String message
) {
}
