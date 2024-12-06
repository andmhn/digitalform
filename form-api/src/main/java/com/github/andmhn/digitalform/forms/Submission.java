package com.github.andmhn.digitalform.forms;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table
class Submission{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany
    @JoinColumn(name = "fk_submission", referencedColumnName = "id")
    private List<Answer> answers;
}