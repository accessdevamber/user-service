package ecommerce.user_service.repo;

import ecommerce.user_service.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    Optional<IdempotencyKey> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query(value = """
            INSERT IGNORE INTO idempotency_keys
                (idempotency_key,
                 request_hash,
                 status,
                 created_at,
                 updated_at)
            VALUES
                (:idempotencyKey,
                 :requestHash,
                 'IN_PROGRESS',
                 CURRENT_TIMESTAMP,
                 CURRENT_TIMESTAMP)
            """, nativeQuery = true)
    int tryClaim(
            @Param("idempotencyKey") String idempotencyKey,
            @Param("requestHash") String requestHash
    );
}
