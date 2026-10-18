package ecommerce.user_service.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

//    public UserNotFoundException(Long id) {
//        super("User not found with id: " + id);
//    }

    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String firstName) {
        super("User not found with firstName: " + firstName);
    }
}
