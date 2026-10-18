package ecommerce.user_service.repo;

import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByStatus(UserStatus status);

    boolean existsByEmail(String email);

//    boolean existsByEmailAndIdNot(
//            String email,
//            Long id
//    );

    boolean existsByEmailAndIdNot(
            String email,
            UUID id
    );

    List<User> findByFirstName(String firstName);

    //if (cursor == null)
    List<User> findByStatusOrderByIdAsc(
            UserStatus status,
            Pageable pageable
    );

    //SELECT *
    //FROM users
    //WHERE status = 'ACTIVE'
    //  AND id > 10
    //ORDER BY id ASC
    //LIMIT 3;
//    List<User> findByStatusAndIdGreaterThanOrderByIdAsc(
//            UserStatus status,
//            Long id,
//            Pageable pageable
//    );

    //if (cursor == null) is false
    List<User> findByStatusAndIdGreaterThanOrderByIdAsc(
            UserStatus status,
            UUID id,
            Pageable pageable
    );

    //if (cursor == null) is false
    @Query("""
       SELECT u
       FROM User u
       WHERE u.status = :status
         AND u.id > :cursor
       ORDER BY u.id ASC
       """)
    List<User> findUsersAfterCursor(
            @Param("status") UserStatus status,
            @Param("cursor") UUID cursor,
            Pageable pageable
    );

    //if (cursor == null) is false
    @Query(value = """
       SELECT *
       FROM users
       WHERE status = :status
         AND id > UUID_TO_BIN(:cursor)
       ORDER BY id ASC
       LIMIT :limit
       """, nativeQuery = true)
    List<User> findUsersAfterCursorNative(
            @Param("status") String status,
            //@Param("cursor") UUID cursor,
            @Param("cursor") String cursor,
            @Param("limit") int limit
    );

    //Here JPQL uses entity field names:
    //
    //User
    //status
    //id
    //
    //not DB table/column names.
//    @Query("""
//       SELECT u
//       FROM User u
//       WHERE u.status = :status
//         AND u.id > :cursor
//       ORDER BY u.id ASC
//       """)
//    List<User> findUsersAfterCursor(
//            @Param("status") UserStatus status,
//            @Param("cursor") Long cursor,
//            Pageable pageable
//    );



//    @Query(value = """
//       SELECT *
//       FROM users
//       WHERE status = :status
//         AND id > :cursor
//       ORDER BY id ASC
//       LIMIT :limit
//       """, nativeQuery = true)
//    List<User> findUsersAfterCursorNative(
//            @Param("status") String status,
//            @Param("cursor") Long cursor,
//            @Param("limit") int limit
//    );







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

//    @Query("""
//       SELECT u
//       FROM User u
//       WHERE u.status = :status
//         AND (
//                u.firstName > :cursorFirstName
//                OR (
//                    u.firstName = :cursorFirstName
//                    AND u.id > :cursorId
//                )
//             )
//       ORDER BY u.firstName ASC, u.id ASC
//       """)
//    List<User> findAfterFirstNameAsc(
//            @Param("status") UserStatus status,
//            @Param("cursorFirstName") String cursorFirstName,
//            @Param("cursorId") Long cursorId,
//            Pageable pageable
//    );



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

//    @Query("""
//       SELECT u
//       FROM User u
//       WHERE u.status = :status
//         AND (
//                u.firstName < :cursorFirstName
//                OR (
//                    u.firstName = :cursorFirstName
//                    AND u.id < :cursorId
//                )
//             )
//       ORDER BY u.firstName DESC, u.id DESC
//       """)
//    List<User> findAfterFirstNameDesc(
//            @Param("status") UserStatus status,
//            @Param("cursorFirstName") String cursorFirstName,
//            @Param("cursorId") Long cursorId,
//            Pageable pageable
//    );



    List<User> findByStatusOrderByFirstNameAsc(
            UserStatus status,
            Pageable pageable
    );

    List<User> findByStatusOrderByFirstNameDesc(
            UserStatus status,
            Pageable pageable
    );

    //direction asc and cursorFirstName == null
    List<User> findByStatusOrderByFirstNameAscIdAsc(
            UserStatus status,
            Pageable pageable
    );

    //direction asc and cursorFirstName and id both != null
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
            @Param("cursorId") UUID cursorId,
            Pageable pageable
    );

    //direction desc and cursorFirstName == null
    List<User> findByStatusOrderByFirstNameDescIdDesc(
            UserStatus status,
            Pageable pageable
    );

    //direction desc and cursorFirstName and id both != null
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
            @Param("cursorId") UUID cursorId,
            Pageable pageable
    );
}
