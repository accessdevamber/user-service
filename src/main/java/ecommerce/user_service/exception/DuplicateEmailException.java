package ecommerce.user_service.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super("Email " + message + " already exists");
    }
}
