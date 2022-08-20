package com.goutham.redditservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CommunityAlreadyExistsException extends RuntimeException {
    public CommunityAlreadyExistsException(String message) {
        super(message);
    }

    public CommunityAlreadyExistsException(String message, Exception exception) {
        super(message, exception);
    }
}
