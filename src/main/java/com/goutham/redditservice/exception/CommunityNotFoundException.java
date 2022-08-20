package com.goutham.redditservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CommunityNotFoundException extends RuntimeException {
    public CommunityNotFoundException(String message) {
        super(message);
    }

    public CommunityNotFoundException(String message, Exception exception) {
        super(message, exception);
    }
}
