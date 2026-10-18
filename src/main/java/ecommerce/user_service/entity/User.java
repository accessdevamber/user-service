package ecommerce.user_service.entity;

import ecommerce.user_service.utility.UuidV7Generator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class User {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    //@NotBlank(message = "First name cannot be blank")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false)//name = "password_hash" is column name in db
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)//Maximum 20 characters, not exactly 20.
    private UserStatus status;

    @Enumerated(EnumType.STRING)//store as string
    @Column(nullable = false, length = 20)
    private UserRole role;

    //@Generated(event = EventType.INSERT)
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    //@Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UuidV7Generator.generate();
        }
        log.info("uuid generated : {}", id);
    }
}
