package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(
         """
         SELECT new com.github.andmhn.digitalform.forms.dto.QuestionResponse(q.id , q.query , q.required , q.type , q.choices)
          FROM Question q WHERE q.form = :form
          ORDER BY q.id
         """
    )
    List<QuestionResponse> getAllByFormDTO(Form form);
}
