package ecommerce.user_service.repo;

import ecommerce.user_service.entity.User;
import ecommerce.user_service.entity.UserRole;
import ecommerce.user_service.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<User> findUsersAfterCursor(
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
    }
}
