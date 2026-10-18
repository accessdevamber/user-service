package ecommerce.user_service.entity;

import ecommerce.user_service.utility.UuidV7Generator;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_import_rejections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserImportRejection {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "job_execution_id")
    private Long jobExecutionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private ImportFailureStage stage;

    @Column(name = "email")
    private String email;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "error_type", nullable = false)
    private String errorType;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UuidV7Generator.generate();
        }
        log.info("uuid generated : {}", id);
    }
}