package ecommerce.user_service.service;

import ecommerce.user_service.dto.UpdateUserRequest;
import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.exception.DuplicateEmailException;
import ecommerce.user_service.exception.UserNotFoundException;
import ecommerce.user_service.mapper.UserMapper;
import ecommerce.user_service.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

//        User user = userMapper.toEntity(request);
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
        //User savedUser = userRepository.save(user);
        User savedUser = userRepository.save(userMapper.toEntity(request));
        //return toResponse(savedUser);
        UserResponse response = userMapper.toResponse(savedUser);
        log.info("Saved user : {}", objectMapper.writeValueAsString(response));
        return response;
    }

    public UserResponse fetchUserById(Long id) {

//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        UserResponse response = userMapper.toResponse(user);
        log.info("Fetched user : {}", objectMapper.writeValueAsString(response));
        return response;
    }

    public List<UserResponse> fetchAllUsers() {

        List<UserResponse> userResponseList = userRepository.findAll()
                .stream()
                //.map(this::toResponse)
                .map(userMapper::toResponse)
                .toList();
        log.info("Fetched users count = {}", userResponseList.size());
        //log.info("Fetched all users normal list logging : {}", userResponseList);
        log.info("Fetched all users logging using jackson ObjectMapper: {}", objectMapper.writeValueAsString(userResponseList));
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

        Set<String> emails = new HashSet<>();
        for (UserRequest request : userRequestList) {
            if (!emails.add(request.email())) {
                log.warn("duplicate emails present in the input list : {}", request.email());
                throw new DuplicateEmailException(request.email());
            }
            if (userRepository.existsByEmail(request.email())) {
                log.warn("{} emails already exists in the DB", request.email());
                throw new DuplicateEmailException(request.email());
            }
        }


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
        log.info("Saved users : {}", objectMapper.writeValueAsString(userResponseList));
        return userResponseList;
    }

    public UserResponse updateUserStatus(Long id, UserStatus status) {

//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("id not found"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User status updated. userId={}, status={}", savedUser.getId(), savedUser.getStatus());
        //UserResponse response = userMapper.toResponse(savedUser);
        return userMapper.toResponse(savedUser);
    }

//    public List<UserResponse> filterByUserStatus(String userStatus) {
//        log.info(".....");
//        List<UserResponse> usersByStatus = userRepository.findByStatus(UserStatus.from(userStatus))
//                .stream()
//                .map(userMapper::toResponse)
//                .toList();
//        //log.info("usersByStatus " + usersByStatus + "{}", usersByStatus);
//        log.info("Users filtered by status. status={}, users={}", userStatus, objectMapper.writeValueAsString(usersByStatus));
//        return usersByStatus;
//    }

    public List<UserResponse> filterByUserStatus(UserStatus userStatus) {
        List<UserResponse> usersByStatus = userRepository.findByStatus(userStatus)
                .stream()
                .map(userMapper::toResponse)
                .toList();
        //log.info("usersByStatus " + usersByStatus + "{}", usersByStatus);
        log.info("Users filtered by status. status={}, users={}", userStatus, objectMapper.writeValueAsString(usersByStatus));
        return usersByStatus;
    }

    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("deleted user with id {}", id);
            //return "deleted user with id" + id;
        } else {
            log.warn("user with id {} doesn't exist", id);
            throw new UserNotFoundException(id);
        }
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        boolean emailTakenByAnotherUser = userRepository.existsByEmailAndIdNot(request.email(), id);
        if (emailTakenByAnotherUser) {
            throw new DuplicateEmailException(request.email());
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setEmail(request.email());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        UserResponse response = userMapper.toResponse(updatedUser);
        log.info("User updated successfully for userId = {} -> {}", id, objectMapper.writeValueAsString(response));
        return response;
    }
}
