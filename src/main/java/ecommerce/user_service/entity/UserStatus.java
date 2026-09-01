package ecommerce.user_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import ecommerce.user_service.exception.InvalidUserStatusException;

import java.util.Locale;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    @JsonCreator
    public static UserStatus from(String value) {
        try {
            return UserStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidUserStatusException(value);
        }
    }
}
