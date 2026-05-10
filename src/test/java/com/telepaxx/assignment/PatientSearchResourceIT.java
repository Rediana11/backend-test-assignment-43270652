package com.telepaxx.assignment;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the /search endpoint.
 * Runs the full Quarkus application with real DICOM files from test-data/roster.
 */
@QuarkusTest
class PatientSearchResourceIT {

    @Test
    void searchByPatientId_returnsMatchingRecords() {
        given()
            .queryParam("patientId", "P1001")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("[0].patientId", equalTo("P1001"));
    }

    @Test
    void searchByLastName_returnsMatchingRecords() {
        given()
            .queryParam("lastName", "Weiss")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1))
            .body("[0].lastName", equalTo("Weiss"));
    }

    @Test
    void searchByLastName_prefixMatch() {
        given()
            .queryParam("lastName", "Wei")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    void searchByBothCriteria_returnsFilteredResults() {
        given()
            .queryParam("patientId", "P2004")
            .queryParam("lastName", "Weiss")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].patientId", equalTo("P2004"));
    }

    @Test
    void searchWithNoParams_returns400() {
        given()
        .when()
            .get("/search")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("message", containsString("At least one search parameter"));
    }

    @Test
    void searchWithBlankParams_returns400() {
        given()
            .queryParam("patientId", "")
            .queryParam("lastName", "")
        .when()
            .get("/search")
        .then()
            .statusCode(400);
    }

    @Test
    void searchWithNoResults_returns200WithEmptyList() {
        given()
            .queryParam("patientId", "NONEXISTENT_ID_99999")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    void responseContainsExpectedFields() {
        given()
            .queryParam("patientId", "P1001")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("[0].patientId", notNullValue())
            .body("[0].lastName", notNullValue())
            .body("[0].firstName", notNullValue())
            .body("[0].fileName", notNullValue());
    }
}
