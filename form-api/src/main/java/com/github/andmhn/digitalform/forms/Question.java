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

    private Integer index;
    private String query;
    private boolean required;
    private String type;
    private List<String> choices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private Form form;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "fk_question", referencedColumnName = "id")
    private List<Answer> answers;
}