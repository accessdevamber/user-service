package ecommerce.user_service.service;

import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserRole;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest request) {

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .passwordHash(request.password()) // temporary
                .status(UserStatus.ACTIVE)
                .role(UserRole.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        log.info("Saved user : {}", savedUser);
        return toResponse(savedUser);
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));

        log.info("Fetched user : {}", user);
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
