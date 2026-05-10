package com.telepaxx.assignment.exception;

import com.telepaxx.assignment.exception.model.ErrorResponse;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import io.quarkus.logging.Log;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof BadRequestException) {
            return buildResponse(400, "Bad Request", exception.getMessage());
        }

        Log.errorf("Unhandled exception: %s", exception.getMessage());
        return buildResponse(500, "Internal Server Error", "An unexpected error occurred");
    }

    private Response buildResponse(int status, String error, String message) {
        ErrorResponse errorResponse = ErrorResponse.of(status, error, message);
        return Response.status(status).entity(errorResponse).build();
    }
}
