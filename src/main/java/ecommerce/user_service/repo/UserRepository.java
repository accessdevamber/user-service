package ecommerce.user_service.repo;

import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatus(UserStatus status);
}
