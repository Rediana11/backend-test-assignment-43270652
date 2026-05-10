package com.telepaxx.assignment;

import com.telepaxx.assignment.model.PaginatedResponse;
import com.telepaxx.assignment.model.PatientRecord;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * HTTP endpoint for patient search.
 *
 * Requirements:
 *   - Accept search criteria: PatientID, last name, or both
 *   - Return a list of matching DICOM files with metadata you consider relevant
 *   - Handle the case where no results are found
 *
 * Important implementation decisions should be documented in NOTES.md.
 */
@Path("/search")
public class PatientSearchResource {

    @Inject
    PatientSearchService searchService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(
            @QueryParam("patientId") String patientId,
            @QueryParam("lastName") String lastName,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        List<PatientRecord> results = searchService.search(patientId, lastName);

        if (results.isEmpty()) {
            return Response.noContent().build();
        }

        PaginatedResponse<PatientRecord> response = PaginatedResponse.of(results, page, pageSize);
        return Response.ok(response).build();
    }
}
