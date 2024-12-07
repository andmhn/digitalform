package com.github.andmhn.digitalform.forms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String query;
    private boolean required;
    private String type;
    private List<String> choices;

    @ManyToOne
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private Form form;

    @OneToMany
    @JoinColumn(name = "fk_question", referencedColumnName = "id")
    private List<Answer> answers;
}