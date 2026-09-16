package ecommerce.user_service.repo;

import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<User> findByFirstName(String firstName);

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

    //Here JPQL uses entity field names:
    //
    //User
    //status
    //id
    //
    //not DB table/column names.
    @Query("""
       SELECT u
       FROM User u
       WHERE u.status = :status
         AND u.id > :cursor
       ORDER BY u.id ASC
       """)
    List<User> findUsersAfterCursor(
            @Param("status") UserStatus status,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query(value = """
       SELECT *
       FROM users
       WHERE status = :status
         AND id > :cursor
       ORDER BY id ASC
       LIMIT :limit
       """, nativeQuery = true)
    List<User> findUsersAfterCursorNative(
            @Param("status") String status,
            @Param("cursor") Long cursor,
            @Param("limit") int limit
    );

    List<User> findByStatusOrderByIdAsc(
            UserStatus status,
            Pageable pageable
    );


    //status = ACTIVE
    //AND first_name > cursor
    //ORDER BY first_name ASC
    @Query("""
       SELECT u
       FROM User u
       WHERE u.status = :status
         AND u.firstName > :cursor
       ORDER BY u.firstName ASC
       """)
    List<User> findAfterFirstNameAsc(
            @Param("status") UserStatus status,
            @Param("cursor") String cursor,
            Pageable pageable
    );

    @Query("""
       SELECT u
       FROM User u
       WHERE u.status = :status
         AND (
                u.firstName > :cursorFirstName
                OR (
                    u.firstName = :cursorFirstName
                    AND u.id > :cursorId
                )
             )
       ORDER BY u.firstName ASC, u.id ASC
       """)
    List<User> findAfterFirstNameAsc(
            @Param("status") UserStatus status,
            @Param("cursorFirstName") String cursorFirstName,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
       SELECT u
       FROM User u
       WHERE u.status = :status
         AND u.firstName < :cursor
       ORDER BY u.firstName DESC
       """)
    List<User> findAfterFirstNameDesc(
            @Param("status") UserStatus status,
            @Param("cursor") String cursor,
            Pageable pageable
    );

    @Query("""
       SELECT u
       FROM User u
       WHERE u.status = :status
         AND (
                u.firstName < :cursorFirstName
                OR (
                    u.firstName = :cursorFirstName
                    AND u.id < :cursorId
                )
             )
       ORDER BY u.firstName DESC, u.id DESC
       """)
    List<User> findAfterFirstNameDesc(
            @Param("status") UserStatus status,
            @Param("cursorFirstName") String cursorFirstName,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    List<User> findByStatusOrderByFirstNameAsc(
            UserStatus status,
            Pageable pageable
    );

    List<User> findByStatusOrderByFirstNameDesc(
            UserStatus status,
            Pageable pageable
    );

    List<User> findByStatusOrderByFirstNameAscIdAsc(
            UserStatus status,
            Pageable pageable
    );

    List<User> findByStatusOrderByFirstNameDescIdDesc(
            UserStatus status,
            Pageable pageable
    );
}
