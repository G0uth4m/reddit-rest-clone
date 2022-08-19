package com.goutham.redditservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AppUserNotFoundException extends RuntimeException {
    public AppUserNotFoundException(String message) {
        super(message);
    }

    public AppUserNotFoundException(String message, Exception exception) {
        super(message, exception);
    }
}
