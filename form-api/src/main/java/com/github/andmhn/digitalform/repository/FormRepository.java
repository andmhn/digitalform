package com.github.andmhn.digitalform.repository;

import com.github.andmhn.digitalform.dto.FormResponse;
import com.github.andmhn.digitalform.entity.Form;
import com.github.andmhn.digitalform.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FormRepository {
    private final JdbcTemplate jdbcTemplate;

    public Form update(Form form) {
        return jdbcTemplate.queryForObject(
                "UPDATE form SET " +
                    "header = ?, description = ?, unlisted = ?, published = ? "+
                    "WHERE id = ? RETURNING *",
                (rs, q) -> new Form(
                        rs.getLong("id"),
                        rs.getString("header"),
                        rs.getString("description"),
                        rs.getBoolean("unlisted"),
                        rs.getBoolean("published"),
                        rs.getLong("fk_user")
                ),
                form.getHeader(), form.getDescription(), form.getUnlisted(), form.getPublished(), form.getId()
        );
    }

    public Form save(Form form) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO form(header, description, unlisted, published, fk_user) VALUES (?, ?, ?, ?, ?) RETURNING *",
                (rs, q) -> new Form(
                        rs.getLong("id"),
                        rs.getString("header"),
                        rs.getString("description"),
                        rs.getBoolean("unlisted"),
                        rs.getBoolean("published"),
                        rs.getLong("fk_user")
                ),
                form.getHeader(), form.getDescription(), form.getUnlisted(), form.getPublished(), form.getFk_user()
        );
    }

    public void delete(Form form) {
        jdbcTemplate.update("DELETE FROM form WHERE id = ?" , form.getId());
    }

    public Optional<Form> findById(Long id) {
        try {
            Form form = jdbcTemplate.queryForObject(
                    "SELECT * FROM form WHERE id = ?",
                    (rs, rowId) -> new Form(
                            rs.getLong("id"),
                            rs.getString("header"),
                            rs.getString("description"),
                            rs.getBoolean("unlisted"),
                            rs.getBoolean("published"),
                            rs.getLong("fk_user")
                    ),
                    id
            );
            if (form != null) return Optional.of(form);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    public Optional<FormResponse> findByIdDTO(Long id){
        String sql = "SELECT f.id, f.header, f.description, f.unlisted, f.published, u.email " +
                "FROM form AS f " +
                "LEFT JOIN users AS u " +
                "ON f.fk_user = u.id " +
                "WHERE f.id = ?";
        try {
            FormResponse formResponse = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowId) -> new FormResponse(
                            rs.getLong("id"),
                            rs.getString("header"),
                            rs.getString("description"),
                            rs.getBoolean("unlisted"),
                            rs.getBoolean("published"),
                            rs.getString("email")
                    ),
                    id
            );
            if (formResponse != null) return Optional.of(formResponse);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    public List<FormResponse> findAllByUserDTO(User user){
        String sql = "SELECT f.id, f.header, f.description, f.unlisted, f.published, u.email " +
                "FROM users AS u " +
                "JOIN form AS f " +
                "ON f.fk_user = u.id " +
                "WHERE u.id = ?";
        List<FormResponse> formResponses = List.of();
        try {
            formResponses = jdbcTemplate.query(
                    sql,
                    (rs, rowId) -> new FormResponse(
                            rs.getLong("id"),
                            rs.getString("header"),
                            rs.getString("description"),
                            rs.getBoolean("unlisted"),
                            rs.getBoolean("published"),
                            rs.getString("email")
                    ),
                    user.getId()
            );
        } catch (DataAccessException ignored) {
        }
        return formResponses;
    }

    public List<FormResponse> findAllPublishedByUserDTO(User user, Boolean published){
        String sql = "SELECT f.id, f.header, f.description, f.unlisted, f.published, u.email " +
                "FROM form AS f " +
                "LEFT JOIN users AS u " +
                "ON f.fk_user = u.id " +
                "WHERE u.id = ? AND f.published = ?";
        List<FormResponse> formResponses = List.of();
        try {
            formResponses = jdbcTemplate.query(
                    sql,
                    (rs, rowId) -> new FormResponse(
                            rs.getLong("id"),
                            rs.getString("header"),
                            rs.getString("description"),
                            rs.getBoolean("unlisted"),
                            rs.getBoolean("published"),
                            rs.getString("email")
                    ),
                    user.getId(), published
            );
        } catch (DataAccessException ignored) {
        }
        return formResponses;
    }

    public List<FormResponse> findAllByUnlistedDTO(boolean isUnlisted){
        String sql = "SELECT f.id, f.header, f.description, f.unlisted, f.published, u.email " +
                "FROM form AS f " +
                "LEFT JOIN users AS u " +
                "ON f.fk_user = u.id " +
                "WHERE f.published = true AND f.unlisted = ?";
        List<FormResponse> formResponses = List.of();
        try {
            formResponses = jdbcTemplate.query(
                    sql,
                    (rs, rowId) -> new FormResponse(
                            rs.getLong("id"),
                            rs.getString("header"),
                            rs.getString("description"),
                            rs.getBoolean("unlisted"),
                            rs.getBoolean("published"),
                            rs.getString("email")
                    ),
                    isUnlisted
            );
        } catch (DataAccessException ignored) {
        }
        return formResponses;
    }
}
