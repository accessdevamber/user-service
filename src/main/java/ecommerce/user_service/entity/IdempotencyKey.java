package ecommerce.user_service.entity;

import ecommerce.user_service.utility.UuidV7Generator;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "idempotency_keys",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_idempotency_key",
                        columnNames = "idempotency_key"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class IdempotencyKey {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdempotencyStatus status;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UuidV7Generator.generate();
        }
        log.info("uuid generated : {}", id);
    }
}
