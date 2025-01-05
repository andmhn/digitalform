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

    public Optional<User> findById(Long id) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE id = ?",
                    (rs, rowId) -> new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    ),
                    id
            );
            if (user != null) return Optional.of(user);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE email = ?",
                    (rs, rowId) -> new User(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    ),
                    email
            );
            if (user != null) return Optional.of(user);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        Long exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                (rs, q) -> rs.getLong("count"),
                email
        );
        return exists != null && !exists.equals(0L);
    }

    public User save(User user) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO users(email, name, password) VALUES (?, ?, ?) RETURNING *",
                (rs, q) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                ),
                user.getEmail(), user.getName(), user.getPassword()
        );
    }

    public void delete(User user) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());
    }
}
