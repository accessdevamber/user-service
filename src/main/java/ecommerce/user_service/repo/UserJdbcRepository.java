package ecommerce.user_service.repo;

import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserRole;
import ecommerce.user_service.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /*public List<User> findUsersAfterCursor(
            UserStatus status,
            Long cursor,
            int limit) {

        String sql = """
                SELECT *
                FROM users
                WHERE status = ?
                  AND id > ?
                ORDER BY id ASC
                LIMIT ?
                """;

//        return jdbcTemplate.query(
//                sql,
//                (rs, rowNum) -> {
//                    User user = new User();
//
//                    user.setId(rs.getLong("id"));
//                    user.setFirstName(rs.getString("first_name"));
//                    user.setLastName(rs.getString("last_name"));
//                    user.setEmail(rs.getString("email"));
//                    user.setPhone(rs.getString("phone"));
//                    user.setPasswordHash(rs.getString("password_hash"));
//                    user.setStatus(UserStatus.valueOf(rs.getString("status")));
//                    user.setRole(UserRole.valueOf(rs.getString("role")));
//                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//                    user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//                    return user;
//                },
//                status.name(),
//                cursor,
//                limit
//        );

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(User.class),
                status.name(),
                cursor,
                limit
        );
    }*/

    public List<User> findUsersAfterCursor(
            UserStatus status,
            UUID cursor,
            int limit) {

        String sql = """
            SELECT
                BIN_TO_UUID(id) AS id,
                first_name,
                last_name,
                email,
                phone,
                password_hash,
                status,
                role,
                created_at,
                updated_at
            FROM users
            WHERE status = ?
              AND id > UUID_TO_BIN(?)
            ORDER BY id ASC
            LIMIT ?
            """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    User user = new User();

                    user.setId(UUID.fromString(rs.getString("id")));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setPasswordHash(rs.getString("password_hash"));

                    user.setStatus(
                            UserStatus.valueOf(rs.getString("status"))
                    );

                    user.setRole(
                            UserRole.valueOf(rs.getString("role"))
                    );

                    user.setCreatedAt(
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    var updatedAt = rs.getTimestamp("updated_at");
                    user.setUpdatedAt(
                            updatedAt != null ? updatedAt.toLocalDateTime() : null
                    );

                    return user;
                },
                status.name(),
                cursor.toString(),
                limit
        );
    }
}
