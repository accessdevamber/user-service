package ecommerce.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_import_rejections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserImportRejection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}