package com.github.andmhn.digitalform.forms;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String Header;
    private String Description;

    @OneToMany
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private List<Question> questions;

    @OneToMany
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private List<Submission> submissions;
}