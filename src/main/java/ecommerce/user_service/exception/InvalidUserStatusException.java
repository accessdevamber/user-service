package ecommerce.user_service.exception;

public class InvalidUserStatusException extends RuntimeException {

    public InvalidUserStatusException(String status) {
        super("Invalid user status: " + status);
    }
}
