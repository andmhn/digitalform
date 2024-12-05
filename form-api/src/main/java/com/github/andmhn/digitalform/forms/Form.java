package com.github.andmhn.digitalform.forms;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String Header;
    String Description;

    @OneToMany
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    List<Question> questions;
}