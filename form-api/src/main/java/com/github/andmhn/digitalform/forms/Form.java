package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String header;
    private String description;
    private Boolean unlisted;

    @ManyToOne
    @JoinColumn(name = "owner_email", referencedColumnName = "email")
    private User user;

    @OneToMany
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private List<Question> questions;

    @OneToMany
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private List<Submission> submissions;
}