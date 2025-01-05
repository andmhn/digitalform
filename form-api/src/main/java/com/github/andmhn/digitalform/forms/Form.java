package com.github.andmhn.digitalform.forms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Form {
    private Long id;
    private String header;
    private String description;
    private Boolean unlisted;
    private Boolean published;

    private Long fk_user;
}