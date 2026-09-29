package ecommerce.user_service.exception;

public class InvalidUserImportException extends RuntimeException {

    public InvalidUserImportException(String message) {
        super(message);
    }
}