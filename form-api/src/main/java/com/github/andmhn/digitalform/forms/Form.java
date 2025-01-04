package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String header;
    private String description;
    private Boolean unlisted;
    private Boolean published;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_email", referencedColumnName = "email")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private List<Question> questions;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "fk_form", referencedColumnName = "id")
    private List<Submission> submissions;
}