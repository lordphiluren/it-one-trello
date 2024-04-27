package ru.sushchenko.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RestException {
    private static final String ID_PREFIX = "Task wasn't found by id: %s";
    public TaskNotFoundException(UUID id) {
        super(String.format(ID_PREFIX, id));
    }

}
