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
import java.util.ArrayList;
import java.util.List;

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

    public UserResponse fetchUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        log.info("Fetched user : {}", user);
        return toResponse(user);
    }

    public List<UserResponse> fetchAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        //streams way
//        users.stream()
//                //.map(u -> toResponse(u))
//                .map(this::toResponse)
//                .map(userResponseList::add)



        for (User user : users) {
            userResponseList.add(toResponse(user));
        }
        log.info("Fetched all users : {}", users);
        return userResponseList;
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

//    private List<UserResponse> toResponseList(List<User> users) {
//
//        List<UserResponse> userResponseList = new ArrayList<>();
//        for (User user : users) {
//            UserResponse userResponse = new UserResponse(
//                    user.getId(),
//                    user.getFirstName(),
//                    user.getLastName(),
//                    user.getEmail(),
//                    user.getPhone(),
//                    user.getStatus(),
//                    user.getRole(),
//                    user.getCreatedAt(),
//                    user.getUpdatedAt());
//            userResponseList.add(userResponse);
//        }
//        return userResponseList;
//    }

    public List<UserResponse> createMultipleUsers(List<UserRequest> userRequestList) {

        List<User> userList = new ArrayList<>();
        for (UserRequest userRequest : userRequestList) {
            User user = new User();
            user.setFirstName(userRequest.firstName());
            user.setLastName(userRequest.lastName());
            user.setEmail(userRequest.email());
            user.setPasswordHash(userRequest.password());
            user.setPhone(userRequest.phone());
            user.setStatus(UserStatus.ACTIVE);
            user.setRole(UserRole.CUSTOMER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userList.add(user);
        }
        List<User> savedUsers = userRepository.saveAll(userList);

        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userList) {
            userResponseList.add(toResponse(user));
        }

        log.info("Saved users : {}", savedUsers);
        return userResponseList;
    }
}
