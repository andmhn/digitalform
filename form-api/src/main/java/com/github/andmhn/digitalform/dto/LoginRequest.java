package com.github.andmhn.digitalform.dto;

import lombok.Data;

@Data
public class LoginRequest {
	private String email;
	private String password;
}
