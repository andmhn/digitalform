package com.github.andmhn.digitalform.forms;

import com.github.andmhn.digitalform.forms.dto.FormResponse;
import com.github.andmhn.digitalform.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, UUID> {
    @Query(
            """
            SELECT new com.github.andmhn.digitalform.forms.dto.FormResponse(f.id, f.header , f.description, f.unlisted, f.user.email)
             FROM Form f WHERE f.id = :id
            """
    )
    Optional<FormResponse> findByIdDTO(UUID id);

    @Query(
            """
            SELECT new com.github.andmhn.digitalform.forms.dto.FormResponse(f.id, f.header , f.description, f.unlisted,  f.user.email)
             FROM Form f WHERE f.user = :user
            """
    )
    List<FormResponse> findAllByUserDTO(User user);

    @Query(
            """
            SELECT new com.github.andmhn.digitalform.forms.dto.FormResponse(f.id, f.header , f.description, f.unlisted,  f.user.email)
             FROM Form f WHERE f.unlisted = :isUnlisted
            """
    )
    List<FormResponse> findAllByUnlistedDTO(boolean isUnlisted);
}
