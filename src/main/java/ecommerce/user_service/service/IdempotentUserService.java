package ecommerce.user_service.service;

import ecommerce.user_service.dto.UserRequest;
import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.entity.IdempotencyKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class IdempotentUserService {

    private final IdempotencyService idempotencyService;
    private final RequestHashService requestHashService;
    private final UserService userService;

    public IdempotentUserService(
            IdempotencyService idempotencyService,
            RequestHashService requestHashService,
            UserService userService) {

        this.idempotencyService = idempotencyService;
        this.requestHashService = requestHashService;
        this.userService = userService;
    }

    @Transactional
    public UserResponse createUser(String idempotencyKey, UserRequest request) {

        String requestHash = requestHashService.hash(request);
        // Is this a retry?
        Optional<UserResponse> completedResponse = idempotencyService.findCompletedResponse(idempotencyKey, requestHash);
        if (completedResponse.isPresent()) {
            log.warn("Duplicate user creation request. Returning old response");
            return completedResponse.get();
        }

        // First request
        log.info("Fresh idempotencyKey {} entered", idempotencyKey);
        IdempotencyKey record = idempotencyService.createInProgress(idempotencyKey, requestHash);
        UserResponse response = userService.createUser(request);
        idempotencyService.markCompleted(record, response, HttpStatus.CREATED.value());
        return response;
    }
}
