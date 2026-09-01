package ecommerce.user_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    @JsonCreator
    public static UserStatus from(String value) {
        return UserStatus.valueOf(value.toUpperCase());
    }
}
