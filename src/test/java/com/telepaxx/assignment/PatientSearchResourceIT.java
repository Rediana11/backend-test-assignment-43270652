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
            .body("data.size()", greaterThanOrEqualTo(1))
            .body("data[0].patientId", equalTo("P1001"))
            .body("page", equalTo(1))
            .body("totalResults", greaterThanOrEqualTo(1));
    }

    @Test
    void searchByLastName_returnsMatchingRecords() {
        given()
            .queryParam("lastName", "Weiss")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("data.size()", greaterThanOrEqualTo(1))
            .body("data[0].lastName", equalTo("Weiss"));
    }

    @Test
    void searchByLastName_prefixMatch() {
        given()
            .queryParam("lastName", "Wei")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("data.size()", greaterThanOrEqualTo(1));
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
            .body("data.size()", equalTo(1))
            .body("data[0].patientId", equalTo("P2004"));
    }

    @Test
    void searchWithNoParams_returns400() {
        given()
        .when()
            .get("/search")
        .then()
            .statusCode(400)
            .body("status", equalTo(400));
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
    void searchWithNoResults_returns204NoContent() {
        given()
            .queryParam("patientId", "NONEXISTENT_ID_99999")
        .when()
            .get("/search")
        .then()
            .statusCode(204);
    }

    @Test
    void responseContainsExpectedFields() {
        given()
            .queryParam("patientId", "P1001")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("data[0].patientId", notNullValue())
            .body("data[0].lastName", notNullValue())
            .body("data[0].firstName", notNullValue())
            .body("data[0].fileName", notNullValue());
    }

    @Test
    void paginationWithCustomPageSize() {
        given()
            .queryParam("patientId", "P1001")
            .queryParam("page", 1)
            .queryParam("pageSize", 2)
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("page", equalTo(1))
            .body("pageSize", equalTo(2))
            .body("data.size()", lessThanOrEqualTo(2))
            .body("totalResults", greaterThanOrEqualTo(1))
            .body("totalPages", greaterThanOrEqualTo(1));
    }

    @Test
    void paginationBeyondResults_returns204() {
        given()
            .queryParam("patientId", "P2004")
            .queryParam("page", 100)
            .queryParam("pageSize", 10)
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("data.size()", equalTo(0))
            .body("totalResults", greaterThanOrEqualTo(1));
    }
}
