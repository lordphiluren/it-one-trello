package ru.sushchenko.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ChecklistNotFoundException extends RestException {
    private static final String ID_PREFIX = "Checklist wasn't found by id: %s";

    public ChecklistNotFoundException(UUID id) {
        super(String.format(ID_PREFIX, id));
    }
}