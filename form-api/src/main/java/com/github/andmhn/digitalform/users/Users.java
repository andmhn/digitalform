package com.github.andmhn.digitalform.users;

import com.github.andmhn.digitalform.forms.Form;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Users {
    @Id
    Long id;
    String name;
    String email;
    String password;

    @OneToMany
    @JoinColumn(name = "ownerEmail", referencedColumnName = "id")
    List<Form> forms;
}
