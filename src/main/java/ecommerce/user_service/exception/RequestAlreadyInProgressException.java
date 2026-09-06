package ecommerce.user_service.exception;

public class RequestAlreadyInProgressException extends RuntimeException {

    public RequestAlreadyInProgressException(String key) {
        super("Request with idempotency key is already in progress: " + key);
    }
}
