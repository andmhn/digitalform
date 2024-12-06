package com.github.andmhn.digitalform.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
        super(message);
    }
}
