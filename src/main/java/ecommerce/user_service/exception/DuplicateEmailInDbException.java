package ecommerce.user_service.exception;

public class DuplicateEmailInDbException extends RuntimeException {

    public DuplicateEmailInDbException(String email) {
        super("Email already exists in DB: " + email);
    }
}
