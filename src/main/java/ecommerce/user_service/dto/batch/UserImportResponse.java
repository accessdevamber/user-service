package ecommerce.user_service.dto.batch;

public record UserImportResponse(
        Long jobExecutionId,
        String status
) {
}
