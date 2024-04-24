package ru.sushchenko.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotEnoughPermissionsException extends RestException {
    public NotEnoughPermissionsException(String msg) {
        super(msg);
    }
}
