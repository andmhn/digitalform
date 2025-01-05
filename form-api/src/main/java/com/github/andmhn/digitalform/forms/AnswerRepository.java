package com.github.andmhn.digitalform.forms;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnswerRepository {
    private final JdbcTemplate jdbcTemplate;

    public Answer save(Answer answer) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO answer(answer, fk_question, fk_submission) VALUES (?, ?, ?) RETURNING *",
                (rs, q) -> new Answer(
                        rs.getLong("id"),
                        rs.getString("answer"),
                        rs.getLong("fk_question"),
                        rs.getLong("fk_submission")
                ),
                answer.getAnswer(), answer.getFk_question(), answer.getFk_submission()
        );
    }

    public void delete(Answer answer) {
        jdbcTemplate.update("DELETE FROM answer WHERE id = ?" , answer.getId());
    }

    public List<Answer> findAllBySubmissionID(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM answer WHERE fk_submission = ?",
                (rs, q) -> new Answer(
                        rs.getLong("id"),
                        rs.getString("answer"),
                        rs.getLong("fk_question"),
                        rs.getLong("fk_submission")
                ),
               id
        );
    }
}
