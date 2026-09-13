package ecommerce.user_service.service;

import ecommerce.user_service.dto.*;
import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.exception.DuplicateEmailException;
import ecommerce.user_service.exception.UserNotFoundException;
import ecommerce.user_service.mapper.UserMapper;
import ecommerce.user_service.repo.UserJdbcRepository;
import ecommerce.user_service.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final UserJdbcRepository userJdbcRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "firstName",
            "lastName",
            "email",
            "createdAt",
            "updatedAt"
    );

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
        User savedUser = userRepository.save(userMapper.toEntity(request));
        UserResponse response = userMapper.toResponse(savedUser);
        log.info("Saved user : {}", objectMapper.writeValueAsString(response));
        return response;
    }

    public UserResponse fetchUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        UserResponse response = userMapper.toResponse(user);
        log.info("Fetched user : {}", objectMapper.writeValueAsString(response));
        return response;
    }

    public List<UserResponse> fetchAllUsers() {

        List<UserResponse> userResponseList = userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
        log.info("Fetched users count = {}", userResponseList.size());
        log.info("Fetched all users logging using jackson ObjectMapper: {}",
                objectMapper.writeValueAsString(userResponseList));
        return userResponseList;
    }

    public PageResponse<UserResponse> fetchAllUsersPaginated(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot be greater than 100");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }
        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        //Pageable pageable = PageRequest.of(page, size);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);
        Page<UserResponse> userResponse = users
                .map(user -> userMapper.toResponse(user));

        //log.info("Fetched users page = {}", userResponseList);
        log.info("Fetched all users paginated. logging using jackson ObjectMapper: {}",
                objectMapper.writeValueAsString(userResponse));
        //pages.getContent() returns a List<Course> containing only the records for the requested page,
        // based on your Pageable (like page number and size).
        log.info("Fetched users paginated content = {}", userResponse.getContent());

        //This method returns the page size, i.e., the maximum number of elements per page
        // as specified in your PageRequest.
        log.info("Fetched users paginated size  i.e per page = {}", userResponse.getSize());

        // example : 4(since total records are 14, and 4 records are on each page.
        // Total 4 pages, with 4 records each on first 3 pages and 2 records on 4th page)
        log.info("Fetched users paginated total pages = {}", userResponse.getTotalPages());

        //This line retrieves the current page number
        //1st page means 0 index
        log.info("Fetched users paginated current page number = {}", userResponse.getNumber());

        //This line returns the number of elements on the current page from a Page<?> object.
        log.info("Fetched users paginated number of elements on the current page = {}",
                userResponse.getNumberOfElements());

        //returns the total count of records in the database that match the query, not just what's on the current page.
        log.info("Fetched users paginated the total count of records = {}", userResponse.getTotalElements());
        return new PageResponse<>(
                userResponse.getContent(),
                userResponse.getNumber(),
                userResponse.getSize(),
                userResponse.getTotalElements(),
                userResponse.getTotalPages(),
                userResponse.isFirst(),
                userResponse.isLast(),
                sortBy,
                direction.toLowerCase()
        );
    }

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
        List<UserResponse> userResponseList = userRepository.saveAll(userList)
                .stream()
                .map(userMapper::toResponse)
                .toList();
        log.info("Saved users : {}", objectMapper.writeValueAsString(userResponseList));
        return userResponseList;
    }

    public UserResponse updateUserStatus(Long id, UserStatus status) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        log.info("User status updated. userId={}, status={}", savedUser.getId(), savedUser.getStatus());
        return userMapper.toResponse(savedUser);
    }

    public List<UserResponse> filterByUserStatus(UserStatus userStatus) {
        List<UserResponse> usersByStatus = userRepository.findByStatus(userStatus)
                .stream()
                .map(userMapper::toResponse)
                .toList();
        log.info("Users filtered by status. status={}, users={}",
                userStatus,
                objectMapper.writeValueAsString(usersByStatus));
        return usersByStatus;
    }

    public CursorPageResponse<UserResponse> filterByUserStatusPaginated(
            UserStatus userStatus,
            Long cursor,
            int size) {

        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        if (size > 100) {
            throw new IllegalArgumentException("Size cannot be greater than 100");
        }

        if (cursor != null && cursor < 0) {
            throw new IllegalArgumentException("Cursor cannot be negative");
        }

        // Ask DB for ONE extra record
        Pageable pageable = PageRequest.of(0, size + 1);
        List<User> users;

        if (cursor == null) {
            log.info("cursor value null");
            users = userRepository.findByStatusOrderByIdAsc(
                    userStatus,
                    pageable);
        } else {
            log.info("cursor value is {}", cursor);
//            users = userRepository.findByStatusAndIdGreaterThanOrderByIdAsc(
//                    userStatus,
//                    cursor,
//                    pageable
//            );

            //JPQL
//            users = userRepository.findUsersAfterCursor(
//                    userStatus,
//                    cursor,
//                    pageable
//            );

            //native query using jpa
//            users = userRepository.findUsersAfterCursorNative(
//                    userStatus.name(),
//                    cursor,
//                    size + 1
//            );

            //jdbcTemplate way
            users = userJdbcRepository.findUsersAfterCursor(
                    userStatus,
                    cursor,
                    size + 1
                    );
        }
        // If we received more than requested size,
        // another batch exists.
        boolean hasNext = users.size() > size;
        List<User> usersToReturn = hasNext
                //public abstract List<E> subList(
                //    int fromIndex,
                //    int toIndex
                //)
                //Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive.

                // (If fromIndex and toIndex are equal, the returned list is empty.)
                // The returned list is backed by this list, so non-structural changes in the returned list are reflected in this list,
                // and vice-versa.
                // The returned list supports all of the optional list operations supported by this list.
                ? users.subList(0, size)//size is exclusive so last record gets removed
                : users;
        Long nextCursor = null;
        //Remove the extra record before returning response.
        //List<User> usersToReturn;
        if (hasNext && !usersToReturn.isEmpty()) {
            log.info("hasNext is true. More records exist");
            // Last returned user's ID becomes the next cursor.
            nextCursor = usersToReturn.get(usersToReturn.size() - 1).getId();
        } else {
            log.info("hasNext is false. End of records");
            //usersToReturn = users;
        }
        List<UserResponse> content = userMapper.toResponse(usersToReturn);
        log.info("Users filtered by status paginated. status={}, users={}",
                userStatus,
                objectMapper.writeValueAsString(content));


//        if (!usersToReturn.isEmpty()) {
//            nextCursor = usersToReturn.get(usersToReturn.size() - 1).getId();
//        }
        log.info("nextCursor is {}", nextCursor);
        return new CursorPageResponse<>(
                content,
                nextCursor,
                hasNext
        );
    }

    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("deleted user with id {}", id);
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
