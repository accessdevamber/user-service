package ecommerce.user_service.controller;

import ecommerce.user_service.dto.UpdateUserRequest;
import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.dto.UserStatusRequest;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/createUser")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.info("====Creating user====");
        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/createMultipleUsers")
    public ResponseEntity<List<UserResponse>> createMultipleUsers(
            @NotEmpty(message = "User list must not be empty")
            @RequestBody List<@Valid UserRequest> userRequestList) {
        log.info("====Creating multiple users====");
        List<UserResponse> userResponseList = userService.createMultipleUsers(userRequestList);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userResponseList);
    }

    @GetMapping("/fetchById/{id}")
    public ResponseEntity<UserResponse> fetchUserById(@PathVariable Long id) {
        log.info("====Fetching userById====");
        return ResponseEntity.ok(userService.fetchUserById(id));
    }

    @GetMapping("/fetchAllUsers")
    public ResponseEntity<List<UserResponse>> fetchAllUsers() {
        log.info("====Fetching all users====");
        return ResponseEntity.ok(userService.fetchAllUsers());
    }

    //http://localhost:9090/users/filterByUserStatus?userStatus= -> considers active
    //http://localhost:9090/users/filterByUserStatus -> considers active
    //http://localhost:9090/users/filterByUserStatus?userStatus=null -> considers invalid user status (InvalidUserStatusException)
    //http://localhost:9090/users/filterByUserStatus?userStatus=hello -> considers invalid user status (InvalidUserStatusException)
    @GetMapping("/filterByUserStatus")
    public ResponseEntity<List<UserResponse>> filterByStatus(@RequestParam(defaultValue = "ACTIVE") String userStatus) {
        log.info("====Filtering by user status====");
        return ResponseEntity.ok(userService.filterByUserStatus(userStatus.toUpperCase(Locale.ROOT)));
    }

    //focus not on URL, but the body for this endpoint

    //case (1) : http://localhost:9090/users/22/status -> {
    //  "status": {}
    //} => HttpMessageNotReadableException : Invalid request body: JSON parse error: Cannot deserialize value of type
    // `ecommerce.user_service.entity.UserStatus` from Object value (token `JsonToken.START_OBJECT`)
    // here, type is not string for status in body itself
    //{
    //    "timestamp": "2026-09-02T17:45:51.646505",
    //    "status": 400,
    //    "errorCode": "INVALID_REQUEST_BODY",
    //    "message": "Invalid or malformed request body"
    //}
    //Exactly. For this body:
    //
    //{
    //  "status": {}
    //}
    //
    //HttpMessageNotReadableException is the correct exception.
    //
    //Why? Because Jackson sees:
    //
    //status field → expected UserStatus enum
    //actual value → {}
    //
    //So it tries to do:
    //
    //{} → UserStatus
    //
    //and fails before Bean Validation runs.
    //
    //That is why @NotNull does not help here. @NotNull only runs after Jackson successfully creates the DTO.


    // case (2) : http://localhost:9090/users/22/status -> {
    //  "status": ""
    //} => HttpMessageNotReadableException : Invalid request body: JSON parse error: Cannot construct instance of
    // `ecommerce.user_service.entity.UserStatus`, problem: Invalid user status:
    // here, type is string for status but empty string. So, enum can't be constructed. So this error comes.
    //{
    //    "timestamp": "2026-09-02T17:46:38.732126",
    //    "status": 400,
    //    "errorCode": "INVALID_REQUEST_BODY",
    //    "message": "Invalid or malformed request body"
    //}


    // case (3) : http://localhost:9090/users/22/status -> {
    //  "status": "gggg"
    //} => HttpMessageNotReadableException : Invalid request body: JSON parse error: Cannot construct instance of
    // `ecommerce.user_service.entity.UserStatus`, problem: Invalid user status: gggg
    // here, type is string for status but invalid string. So, enum can't be constructed. So this error comes.
    //Similar to above case
    //{
    //    "timestamp": "2026-09-02T18:02:04.514388",
    //    "status": 400,
    //    "errorCode": "INVALID_REQUEST_BODY",
    //    "message": "Invalid or malformed request body"
    //}

    // case (4) : http://localhost:9090/users/22/status -> {
    //  "status": null
    //}
    //Request validation failed: status: Status is required
    //{
    //    "timestamp": "2026-09-02T18:15:12.014088",
    //    "status": 400,
    //    "errorCode": "VALIDATION_FAILED",
    //    "message": "status: Status is required"
    //}


    //VALID
    // case (5) : http://localhost:9090/users/22/status -> {
    //  "status": "inactive"
    //}
    //User status updated. userId=22, status=INACTIVE
    //{
    //    "id": 22,
    //    "firstName": "Rohan",
    //    "lastName": "Bhatia",
    //    "email": "rohan.bhatia@example.com",
    //    "phone": "9812345603",
    //    "status": "INACTIVE",
    //    "role": "CUSTOMER",
    //    "createdAt": "2026-08-31T18:03:58",
    //    "updatedAt": "2026-09-02T18:20:24.257534"
    //}


    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) {
        log.info("====updateUserStatus====");
        return ResponseEntity.ok(userService.updateUserStatus(id, request.status()));
    }

    //my first version for proper display message
//    @DeleteMapping("deleteById/{id}/")
//    public ResponseEntity<String> deleteById(@PathVariable Long id) {
//        log.info("====deleteById====");
//        return ResponseEntity.ok(userService.deleteById(id));
//    }

    //standard way using 204 no content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("====deleteById====");
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //valid only for firstName,lastName,email and phone
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        log.info("====updateUser====");
        return ResponseEntity.ok(userService.updateUser(id, request));
    }


}
