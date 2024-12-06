package com.github.andmhn.digitalform.forms;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table
class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String answer;
}
