package ru.sushchenko.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class EntityAlreadyExistException extends RestException {
    public EntityAlreadyExistException(String msg) {
        super(msg);
    }
}
