package ecommerce.user_service.exception;

public class DuplicateEmailInCsvException extends RuntimeException {

    public DuplicateEmailInCsvException(String email) {
        super("Duplicate email found in CSV: " + email);
    }
}
