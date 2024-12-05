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
    Long id;

    String query;
    boolean isRequired;
    String type;
    List<String> choices;

    @OneToMany
    @JoinColumn(name = "fk_question", referencedColumnName = "id")
    List<Answer> answers;
}