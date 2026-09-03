package ecommerce.user_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import ecommerce.user_service.exception.InvalidUserStatusException;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED;

    @JsonCreator
    public static UserStatus from(String value) {

        if (value == null) {
            throw new InvalidUserStatusException(null);
        }

        try {
            return UserStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new InvalidUserStatusException(value);
        }
    }
}
