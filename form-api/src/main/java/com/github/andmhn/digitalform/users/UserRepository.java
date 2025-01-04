package com.github.andmhn.digitalform.users;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE email = ?",
                    (rs, rowId) -> new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            null
                    ),
                    email
            );
            if (user != null) return Optional.of(user);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    boolean existsByEmail(String email) {
        Long exists = jdbcTemplate.queryForObject(
                "select count(*) from users where email = ?",
                (rs, q) -> rs.getLong("count"),
                email
        );
        return exists != null && !exists.equals(0L);
    }

    User save(User user) {
        return jdbcTemplate.queryForObject(
                "insert into users(email, name, password) values (?, ?, ?) returning *",
                (rs, q) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        null
                ),
                user.getEmail(), user.getName(), user.getPassword()
        );
    }

    void delete(User user) {
        jdbcTemplate.execute("delete from users where id = " + user.getId());
    }
}
