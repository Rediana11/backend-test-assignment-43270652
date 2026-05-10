package com.telepaxx.assignment;

import com.telepaxx.assignment.model.PatientRecord;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * HTTP endpoint for patient search.
 *
 * TODO: Design and implement the search endpoint.
 *
 * Requirements:
 *   - Accept search criteria: PatientID, last name, or both
 *   - Return a list of matching DICOM files with metadata you consider relevant
 *   - Handle the case where no results are found
 *
 * Important implementation decisions should be documented in NOTES.md.
 *
 * This bootstrap path can be kept or changed if justified.
 */
@Path("/search")
public class PatientSearchResource {

    @Inject
    PatientSearchService searchService;

    @GET
    public Response search(
            @QueryParam("patientId") String patientId,
            @QueryParam("lastName") String lastName) {
     
        List<PatientRecord> results = searchService.search(patientId, lastName);

        return Response.ok(results).build();
    }
}
