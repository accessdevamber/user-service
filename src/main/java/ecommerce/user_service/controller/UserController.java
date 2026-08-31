package ecommerce.user_service.controller;

import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


}
