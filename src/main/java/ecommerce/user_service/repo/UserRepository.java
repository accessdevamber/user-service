package ecommerce.user_service.repo;

import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatus(UserStatus status);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(
            String email,
            Long id
    );

    //SELECT *
    //FROM users
    //WHERE status = 'ACTIVE'
    //  AND id > 10
    //ORDER BY id ASC
    //LIMIT 3;
    List<User> findByStatusAndIdGreaterThanOrderByIdAsc(
            UserStatus status,
            Long id,
            Pageable pageable
    );

    List<User> findByStatusOrderByIdAsc(
            UserStatus status,
            Pageable pageable
    );
}
