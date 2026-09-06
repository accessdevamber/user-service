package ecommerce.user_service.exception;

public class IdempotencyKeyReusedException extends RuntimeException {

    public IdempotencyKeyReusedException(String key) {
        super("Idempotency key has already been used for a different request: " + key);
    }
}
