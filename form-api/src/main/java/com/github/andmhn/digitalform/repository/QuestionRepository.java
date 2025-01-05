package com.github.andmhn.digitalform.repository;

import com.github.andmhn.digitalform.dto.QuestionResponse;
import com.github.andmhn.digitalform.entity.Form;
import com.github.andmhn.digitalform.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.Connection;
import java.sql.JDBCType;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QuestionRepository {
    private final JdbcTemplate jdbcTemplate;

    public Question update(Question question) {
        Array choices = jdbcTemplate.execute(
                (Connection c) -> c.createArrayOf(JDBCType.VARCHAR.getName(), question.getChoices())
        );
        return jdbcTemplate.queryForObject(
                "UPDATE question SET " +
                        "index = ?, query = ?, required = ?, type = ?, choices = ? " +
                        "WHERE id = ? RETURNING *",
                (rs, q) -> {
                    Array savedChoicesArray = rs.getArray("choices");
                    String[] savedChoices = new String[0];
                    if (savedChoicesArray != null)
                        savedChoices = (String[]) savedChoicesArray.getArray();
                    return new Question(
                            rs.getLong("id"),
                            rs.getInt("index"),
                            rs.getString("query"),
                            rs.getBoolean("required"),
                            rs.getString("type"),
                            savedChoices,
                            rs.getLong("fk_form")
                    );
                },
                question.getIndex(), question.getQuery(), question.isRequired(), question.getType(), choices, question.getId()
        );
    }

    public Question save(Question question) {
        Array choices = jdbcTemplate.execute(
                (Connection c) -> c.createArrayOf(JDBCType.VARCHAR.getName(), question.getChoices())
        );
        return jdbcTemplate.queryForObject(
                "INSERT INTO question(index, query , required , type , choices, fk_form) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING *",
                (rs, q) -> {
                    Array savedChoicesArray = rs.getArray("choices");
                    String[] savedChoices = new String[0];
                    if (savedChoicesArray != null)
                        savedChoices = (String[]) savedChoicesArray.getArray();
                    return new Question(
                            rs.getLong("id"),
                            rs.getInt("index"),
                            rs.getString("query"),
                            rs.getBoolean("required"),
                            rs.getString("type"),
                            savedChoices,
                            rs.getLong("fk_form")
                    );
                },
                question.getIndex(), question.getQuery(), question.isRequired(), question.getType(), choices, question.getFk_form()
        );
    }

    public void delete(Question question) {
        jdbcTemplate.update("DELETE FROM question WHERE id = ?", question.getId());
    }

    public Optional<Question> findById(Long id) {
        try {
            Question question = jdbcTemplate.queryForObject(
                    "SELECT * FROM question WHERE id = ?",
                    (rs, rowId) -> {
                        Array savedChoicesArray = rs.getArray("choices");
                        String[] savedChoices = new String[0];
                        if (savedChoicesArray != null)
                            savedChoices = (String[]) savedChoicesArray.getArray();
                        return new Question(
                                rs.getLong("id"),
                                rs.getInt("index"),
                                rs.getString("query"),
                                rs.getBoolean("required"),
                                rs.getString("type"),
                                savedChoices,
                                rs.getLong("fk_form")
                        );
                    },
                    id
            );
            if (question != null) return Optional.of(question);
        } catch (DataAccessException ignored) {
        }
        return Optional.empty();
    }

    public List<QuestionResponse> getAllByFormDTO(Form form) {
        String sql = "SELECT q.id , q.index, q.query , q.required , q.type , q.choices " +
                "FROM question AS q " +
                "WHERE q.fk_form = ? " +
                "ORDER BY q.index";
        List<QuestionResponse> questionResponses = List.of();
        try {
            questionResponses = jdbcTemplate.query(
                    sql,
                    (rs, rowId) -> {
                        Array savedChoicesArray = rs.getArray("choices");
                        String[] savedChoices = new String[0];
                        if (savedChoicesArray != null)
                            savedChoices = (String[]) savedChoicesArray.getArray();
                        return new QuestionResponse(
                                rs.getLong("id"),
                                rs.getInt("index"),
                                rs.getString("query"),
                                rs.getBoolean("required"),
                                rs.getString("type"),
                                savedChoices);
                    },
                    form.getId()
            );
        } catch (DataAccessException ignored) {
        }
        return questionResponses;
    }
}
