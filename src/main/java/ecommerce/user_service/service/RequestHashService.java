package ecommerce.user_service.service;

import ecommerce.user_service.dto.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
@Slf4j
public class RequestHashService {

    private final ObjectMapper objectMapper;

    public RequestHashService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String hash(UserRequest request) {

        try {
            String requestJson = objectMapper.writeValueAsString(request);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(requestJson.getBytes(StandardCharsets.UTF_8));
            String requestHash = HexFormat.of().formatHex(hash);
            log.info("requestHash = {}", requestHash);
            return requestHash;
            //return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            log.warn("Inside NoSuchAlgorithmException catch block {}", ex.getMessage());
            throw new IllegalStateException("Unable to calculate request hash", ex);
        }
    }
}
