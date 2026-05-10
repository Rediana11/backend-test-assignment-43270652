package com.telepaxx.assignment;

import com.telepaxx.assignment.exception.MissingSearchCriteriaException;
import com.telepaxx.assignment.model.PatientRecord;
import com.telepaxx.assignment.roster.RosterLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class PatientSearchServiceTest {

    private PatientSearchService service;

    private static final List<PatientRecord> TEST_PATIENTS = List.of(
            new PatientRecord("P001", "Smith", "John", "1001.dcm"),
            new PatientRecord("P002", "Doe", "Jane", "1002.dcm"),
            new PatientRecord("P003", "Johnson", "Bob", "1003.dcm")
    );

    @BeforeEach
    void setUp() throws Exception {
        service = new PatientSearchService();

        RosterLoader rosterLoader = new RosterLoader();
        Field patientsField = RosterLoader.class.getDeclaredField("patients");
        patientsField.setAccessible(true);
        patientsField.set(rosterLoader, TEST_PATIENTS);

        Field loaderField = PatientSearchService.class.getDeclaredField("rosterLoader");
        loaderField.setAccessible(true);
        loaderField.set(service, rosterLoader);
    }

    @Test
    void searchByPatientId_exactMatch() {
        List<PatientRecord> results = service.search("P001", null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).patientId()).isEqualTo("P001");
    }

    @Test
    void searchByPatientId_caseInsensitive() {
        List<PatientRecord> results = service.search("p001", null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).patientId()).isEqualTo("P001");
    }

    @Test
    void searchByLastName_prefixMatch() {
        List<PatientRecord> results = service.search(null, "Smi");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).lastName()).isEqualTo("Smith");
    }

    @Test
    void searchByLastName_caseInsensitive() {
        List<PatientRecord> results = service.search(null, "SMITH");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).lastName()).isEqualTo("Smith");
    }

    @Test
    void searchByLastName_multipleMatches() {
        List<PatientRecord> results = service.search(null, "Jo");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).lastName()).isEqualTo("Johnson");
    }

    @Test
    void searchByBothCriteria() {
        List<PatientRecord> results = service.search("P001", "Smith");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).patientId()).isEqualTo("P001");
        assertThat(results.get(0).lastName()).isEqualTo("Smith");
    }

    @Test
    void searchByBothCriteria_noMatch() {
        List<PatientRecord> results = service.search("P001", "Doe");

        assertThat(results).isEmpty();
    }

    @Test
    void searchWithNoResults() {
        List<PatientRecord> results = service.search("NONEXISTENT", null);

        assertThat(results).isEmpty();
    }

    @Test
    void searchWithNoCriteria_throwsException() {
        assertThatThrownBy(() -> service.search(null, null))
                .isInstanceOf(MissingSearchCriteriaException.class);
    }

    @Test
    void searchWithBlankCriteria_throwsException() {
        assertThatThrownBy(() -> service.search("", "  "))
                .isInstanceOf(MissingSearchCriteriaException.class);
    }
}
