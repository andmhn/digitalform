package com.github.andmhn.digitalform.forms;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubmissionRepository {
    private final JdbcTemplate jdbcTemplate;

    Optional<Submission> findById(Long id){
        try {
            Submission submission = jdbcTemplate.queryForObject(
                    "SELECT * FROM submission WHERE id = ?",
                    (rs, q) -> new Submission( rs.getLong("id"), rs.getLong("fk_form")),
                    id
            );
            if (submission != null) return Optional.of(submission);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    List<Submission> findByFormId(Long id){
        List<Submission> submission = List.of();
        try {
             submission = jdbcTemplate.query(
                    "SELECT * FROM submission WHERE fk_form = ?",
                    (rs, q) -> new Submission( rs.getLong("id"), rs.getLong("fk_form")),
                    id
            );
        } catch (DataAccessException ignored) {
        }
        return submission;
    }

    Submission save(Submission submission){
        return jdbcTemplate.queryForObject(
                "INSERT INTO submission(fk_form) VALUES (?) RETURNING *",
                (rs, q) -> new Submission(
                        rs.getLong("id"),
                        rs.getLong("fk_form")
                ),
                submission.getFk_form()
        );
    }

    void delete(Submission submission){
        jdbcTemplate.update("DELETE FROM submission WHERE id = ?", submission.getId());
    }
}
