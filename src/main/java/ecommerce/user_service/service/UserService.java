package ecommerce.user_service.service;

import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.mapper.UserMapper;
import ecommerce.user_service.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse createUser(UserRequest request) {

        User user = userMapper.toEntity(request);
//        User user = User.builder()
//                .firstName(request.firstName())
//                .lastName(request.lastName())
//                .email(request.email())
//                .phone(request.phone())
//                .passwordHash(request.password()) // temporary
//                .status(UserStatus.ACTIVE)
//                .role(UserRole.CUSTOMER)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
        User savedUser = userRepository.save(user);
        //return toResponse(savedUser);
        UserResponse response = userMapper.toResponse(user);
        log.info("Saved user : {}", response);
        return response;
    }

    public UserResponse fetchUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        log.info("Fetched user : {}", user);
        //return toResponse(user);
        return userMapper.toResponse(user);
    }

    public List<UserResponse> fetchAllUsers() {

        List<UserResponse> userResponseList = userRepository.findAll()
                .stream()
                //.map(this::toResponse)
                .map(userMapper::toResponse)
                .toList();
        log.info("Fetched all users : {}", userResponseList);
        return userResponseList;
    }

//    private UserResponse toResponse(User user) {
//        return new UserResponse(
//                user.getId(),
//                user.getFirstName(),
//                user.getLastName(),
//                user.getEmail(),
//                user.getPhone(),
//                user.getStatus(),
//                user.getRole(),
//                user.getCreatedAt(),
//                user.getUpdatedAt()
//        );
//    }

    public List<UserResponse> createMultipleUsers(List<UserRequest> userRequestList) {

        List<User> userList = userMapper.toEntityList(userRequestList)
                .stream()
                .toList();

//        List<UserResponse> userResponseList = userRequestList.stream()
//                .map(userMapper::toEntity)
//                .map(userMapper::toResponse)
//                .toList();

/*        List<User> userList = new ArrayList<>();
        for (UserRequest userRequest : userRequestList) {
            User user = new User();
//            user.setFirstName(userRequest.firstName());
//            user.setLastName(userRequest.lastName());
//            user.setEmail(userRequest.email());
//            user.setPasswordHash(userRequest.password());
//            user.setPhone(userRequest.phone());
//            user.setStatus(UserStatus.ACTIVE);
//            user.setRole(UserRole.CUSTOMER);
//            user.setCreatedAt(LocalDateTime.now());
//            user.setUpdatedAt(LocalDateTime.now());
            userMapper.toEntity(userRequest);
            userList.add(user);
        }*/

        List<UserResponse> userResponseList = userRepository.saveAll(userList)
                .stream()
                .map(userMapper::toResponse)
                .toList();
        log.info("Saved users : {}", userResponseList);
        return userResponseList;
    }

    public UserResponse updateUserStatus(Long id, UserStatus status) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("id not found"));
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User status updated. userId={}, status={}", savedUser.getId(), savedUser.getStatus());
        return userMapper.toResponse(savedUser);
    }
}
