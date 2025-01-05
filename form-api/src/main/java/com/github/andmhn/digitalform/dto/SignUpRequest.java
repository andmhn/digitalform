package com.github.andmhn.digitalform.dto;
import lombok.*;

@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
	private String email;
	private String name;
	private String password;
}
