package com.goutham.redditservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AppUserNotMemberOfCommunityException extends RuntimeException {
    public AppUserNotMemberOfCommunityException(String message) {
        super(message);
    }

    public AppUserNotMemberOfCommunityException(String message, Exception exception) {
        super(message, exception);
    }
}
