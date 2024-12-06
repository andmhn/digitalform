package com.github.andmhn.digitalform.exeptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException  extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
        super(message);
    }
}
