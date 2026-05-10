package com.telepaxx.assignment.exception;

public class MissingSearchCriteriaException extends BadRequestException {

    public MissingSearchCriteriaException() {
        super("At least patient id or lastName must be provided");
    }

    public MissingSearchCriteriaException(String message) {
        super(message);
    }
}
