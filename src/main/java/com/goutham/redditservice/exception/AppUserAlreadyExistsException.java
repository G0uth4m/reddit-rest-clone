package com.goutham.redditservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AppUserAlreadyExistsException extends RuntimeException {
    public AppUserAlreadyExistsException(String message) {
        super(message);
    }

    public AppUserAlreadyExistsException(String message, Exception exception) {
        super(message, exception);
    }
}
