package com.telepaxx.assignment.exception;

import jakarta.ws.rs.BadRequestException;

public class MissingSearchCriteriaException extends BadRequestException {

    public MissingSearchCriteriaException() {
        super("At least one search parameter must be provided: patientId or lastName");
    }

    public MissingSearchCriteriaException(String message) {
        super(message);
    }
}
