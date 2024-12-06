package com.github.andmhn.digitalform.forms;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table
class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String query;
    private boolean isRequired;
    private String type;
    private List<String> choices;

    @OneToMany
    @JoinColumn(name = "fk_question", referencedColumnName = "id")
    private List<Answer> answers;
}