package ecommerce.user_service.entity;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    public static UserStatus from(String value) {
        return UserStatus.valueOf(value.toUpperCase());
    }
}
