package com.telepaxx.assignment;

import com.telepaxx.assignment.model.PatientRecord;
import com.telepaxx.assignment.roster.RosterLoader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Searches patient records loaded from the roster source.
 *
 * TODO: Implement this class.
 *
 * Given search criteria (patientId, lastName, or both), return all matching PatientRecords.
 *
 * Important implementation decisions should be documented in NOTES.md.
 */
@ApplicationScoped
public class PatientSearchService {

    @Inject
    RosterLoader rosterLoader;

    public List<PatientRecord> search(String patientId, String lastName) {
        List<PatientRecord> allRecords = rosterLoader.getPatients();

        return allRecords.stream()
                .filter(record -> matchesPatientId(record, patientId))
                .filter(record -> matchesLastName(record, lastName))
                .toList();
    }

    private boolean matchesPatientId(PatientRecord record, String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return true;
        }
        return record.patientId().equalsIgnoreCase(patientId.trim());
    }

    private boolean matchesLastName(PatientRecord record, String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return true;
        }
        return record.lastName().toLowerCase().startsWith(lastName.trim().toLowerCase());
    }
}
