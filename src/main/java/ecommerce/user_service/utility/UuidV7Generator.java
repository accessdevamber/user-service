package ecommerce.user_service.utility;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public final class UuidV7Generator {

    private UuidV7Generator() {
        // Utility class: prevent object creation
    }

    public static UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}