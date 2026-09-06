package ecommerce.user_service.exception;

public enum ErrorCode {
    USER_NOT_FOUND,
    EMAIL_ALREADY_EXISTS,
    INVALID_USER_STATUS,
    INVALID_REQUEST_BODY,
    INVALID_ARGUMENT_TYPE,
    VALIDATION_FAILED,

    //idempotency related error codes
    IDEMPOTENCY_KEY_REUSED,
    REQUEST_ALREADY_IN_PROGRESS,

    //generic error codes
    DATA_INTEGRITY_VIOLATION,
    INTERNAL_SERVER_ERROR
}
