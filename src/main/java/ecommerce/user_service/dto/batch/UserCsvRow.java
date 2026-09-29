package ecommerce.user_service.dto.batch;

public record UserCsvRow(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password
) {
}
