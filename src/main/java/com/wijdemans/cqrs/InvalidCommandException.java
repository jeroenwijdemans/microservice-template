package com.wijdemans.cqrs;

import java.util.ArrayList;
import java.util.List;

public class InvalidCommandException extends RuntimeException {

    private List<Error> errors = new ArrayList<>();

    public InvalidCommandException() {
        super("Command is not valid.");
    }

    public InvalidCommandException(Error error) {
        super("Command is not valid.");
        addMistake(error);
    }

    public void addMistake(Error error) {
        this.errors.add(error);
    }

    @Override
    public String getMessage() {
        return toString();
    }

    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "Mistakes: " + errors;
    }
}
