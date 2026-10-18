package ecommerce.user_service.repo;

import ecommerce.user_service.entity.UserImportRejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
//public interface UserImportRejectionRepository extends JpaRepository<UserImportRejection, Long> {
public interface UserImportRejectionRepository extends JpaRepository<UserImportRejection, UUID> {
}