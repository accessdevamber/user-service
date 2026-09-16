package ecommerce.user_service.service;

import ecommerce.user_service.dto.UserResponse;
import ecommerce.user_service.entity.IdempotencyKey;
import ecommerce.user_service.entity.IdempotencyStatus;
import ecommerce.user_service.exception.IdempotencyKeyReusedException;
import ecommerce.user_service.exception.RequestAlreadyInProgressException;
import ecommerce.user_service.repo.IdempotencyKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class IdempotencyService {

    private final IdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyKeyRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    //1st approach for idempotent user creation
    /*public Optional<UserResponse> findCompletedResponse(String key, String requestHash) {

        Optional<IdempotencyKey> existingOptional = repository.findByIdempotencyKey(key);
        if (existingOptional.isEmpty()) {
            log.info("IdempotencyKey {} not found in DB", key);
            return Optional.empty();
        }
        IdempotencyKey existingIdempotencyKey = existingOptional.get();
        log.info("IdempotencyKey {} already exists in DB", key);
        // Same key but DIFFERENT request
        if (!existingIdempotencyKey.getRequestHash().equals(requestHash)) {
            log.warn("Existing IdempotencyKey {} reused with new request", key);
            log.warn("Potential malicious request");
            throw new IdempotencyKeyReusedException(key);
        }
        // Same key + same request, but currently processing
        if (existingIdempotencyKey.getStatus() == IdempotencyStatus.IN_PROGRESS) {
            log.warn("user creation request already in progress. Wait before retrying.");
            throw new RequestAlreadyInProgressException(key);
        }
        // Same key + same request + completed
        //try {
        UserResponse response = objectMapper.readValue(existingIdempotencyKey.getResponseBody(), UserResponse.class);
        return Optional.of(response);
//        } catch (JsonProcessingException ex) {
//            throw new IllegalStateException("Unable to deserialize stored idempotency response", ex);
//        }
    }*/

    //1st approach for idempotent user creation
    /*public IdempotencyKey createInProgress(String key, String requestHash) {

        LocalDateTime now = LocalDateTime.now();
        IdempotencyKey record = IdempotencyKey.builder()
                .idempotencyKey(key)
                .requestHash(requestHash)
                .status(IdempotencyStatus.IN_PROGRESS)
                .createdAt(now)
                .updatedAt(now)
                .build();
        IdempotencyKey idempotencyKey = repository.save(record);
        log.info("Creating IdempotencyKey with {} status", IdempotencyStatus.IN_PROGRESS);
        return idempotencyKey;
    }*/

//    public void markCompleted(IdempotencyKey record, UserResponse response, int httpStatus) {
//
//        String responseJson = objectMapper.writeValueAsString(response);
//        record.setStatus(IdempotencyStatus.COMPLETED);
//        record.setResponseBody(responseJson);
//        record.setHttpStatus(httpStatus);
//        record.setUpdatedAt(LocalDateTime.now());
//        IdempotencyKey idempotencyKey = repository.save(record);
//        log.info("IdempotencyKey status changed to {} for idempotencyKey {}",
//                IdempotencyStatus.COMPLETED,
//                idempotencyKey.getIdempotencyKey());
//    }

    public void markCompleted(
            IdempotencyKey record,
            Object response,
            int httpStatus
    ) {

        String responseBody = objectMapper.writeValueAsString(response);
        record.setStatus(IdempotencyStatus.COMPLETED);
        record.setResponseBody(responseBody);
        record.setHttpStatus(httpStatus);
        IdempotencyKey idempotencyKey = repository.save(record);
        log.info("IdempotencyKey status changed to {} for idempotencyKey {}",
                IdempotencyStatus.COMPLETED,
                idempotencyKey.getIdempotencyKey());
    }

    public int tryClaim(String idempotencyKey, String requestHash) {

        int inserted = repository.tryClaim(idempotencyKey, requestHash);
        log.info("Idempotency claim result. key={}, inserted={}", idempotencyKey, inserted);
        return inserted;
    }

    public IdempotencyKey getByKey(String idempotencyKey) {

        return repository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new IllegalStateException("Idempotency record not found: " + idempotencyKey));
    }

    public UserResponse getExistingResponse(String idempotencyKey, String requestHash) {

        IdempotencyKey existing = repository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new IllegalStateException("Idempotency record not found after claim collision"));

        // Same key but DIFFERENT request
        if (!existing.getRequestHash().equals(requestHash)) {
            log.warn("idempotencyKey reused for different request. Could be malicious request.");
            throw new IdempotencyKeyReusedException(idempotencyKey);
        }

        // Same key + same request, but still processing
        if (existing.getStatus() == IdempotencyStatus.IN_PROGRESS) {
            log.warn("idempotencyKey {} already claimed by different request. IdempotencyKey creation in progress.",
                    idempotencyKey);
            throw new RequestAlreadyInProgressException(idempotencyKey);
        }

        // Same key + same request + already completed
        String responseBody = existing.getResponseBody();
        log.info("Duplicate request. Returning old response {}", responseBody);
        return objectMapper.readValue(responseBody, UserResponse.class);
    }

    //bulk user method
    public List<UserResponse> getExistingBulkResponse(
            String idempotencyKey,
            String requestHash
    ) {

        IdempotencyKey existing = repository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() ->
                        new IllegalStateException("Idempotency record not found after claim collision"));

        // Same key + DIFFERENT bulk request
        if (!existing.getRequestHash().equals(requestHash)) {
            log.warn("Bulk idempotency key reused for different request. key={}", idempotencyKey);
            throw new IdempotencyKeyReusedException(idempotencyKey);
        }

        // Same key + same request, but original request is processing
        if (existing.getStatus() == IdempotencyStatus.IN_PROGRESS) {
            log.warn("Bulk request already in progress. key={}", idempotencyKey);
            throw new RequestAlreadyInProgressException(idempotencyKey);
        }

        // Same key + same request + COMPLETED
        String responseBody = existing.getResponseBody();
        log.info("Duplicate bulk request. Returning stored response. key={}", idempotencyKey);
        return objectMapper.readValue(
                responseBody,
                objectMapper.getTypeFactory()
                        .constructCollectionType(
                                List.class,
                                UserResponse.class
                        )
        );
    }
}
