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
        try {
            return UserStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            log.warn("Inside catch IllegalArgumentException");
            throw new InvalidUserStatusException(value);
        }
    }
}
