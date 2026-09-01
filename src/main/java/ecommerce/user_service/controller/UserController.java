package ecommerce.user_service.controller;

import ecommerce.user_service.dto.UpdateUserRequest;
import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.dto.UserStatusRequest;
import ecommerce.user_service.entity.UserStatus;
import ecommerce.user_service.service.UserService;
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
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        log.info("====Creating user====");
        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/createMultipleUsers")
    public ResponseEntity<List<UserResponse>> createMultipleUsers(@RequestBody List<UserRequest> userRequestList) {
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

    @GetMapping("/filterByUserStatus")
    public ResponseEntity<List<UserResponse>> filterByStatus(@RequestParam(defaultValue = "ACTIVE") String userStatus) {
        log.info("====Filtering by user status====");
        return ResponseEntity.ok(userService.filterByUserStatus(userStatus.toUpperCase(Locale.ROOT)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id, @RequestBody UserStatusRequest request) {
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
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        log.info("====updateUser====");
        return ResponseEntity.ok(userService.updateUser(id, request));
    }



}
