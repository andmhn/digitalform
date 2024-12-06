package com.github.andmhn.digitalform.users.dto;

import java.util.UUID;

public record AuthResponse(UUID id, String email, String name) {}
