package ecommerce.user_service.controller;

import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/fetchById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("====Fetching userById====");
        return ResponseEntity.ok(userService.getUserById(id));
    }

}
