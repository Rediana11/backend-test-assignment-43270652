package com.telepaxx.assignment.roster;

import com.telepaxx.assignment.model.PatientRecord;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Loads patient-related data from a folder of DICOM files.
 *
 * TODO: Implement this class.
 *
 * Read *.dcm files from the configured roster folder.
 * For each relevant file, extract at minimum:
 *   - PatientID  (DICOM tag: Tag.PatientID) 
 *   - PatientName (DICOM tag: Tag.PatientName) — stored as "FamilyName^GivenName^..."
 *
 * Expose data needed by PatientSearchService.
 *
 * Important implementation decisions should be documented in NOTES.md.
 *
 * Hint: use DicomInputStream from the dcm4che3 library (already on the classpath).
 */
@ApplicationScoped
public class RosterLoader {

    @ConfigProperty(name = "assignment.roster.path")
    String rosterPath;

    private List<PatientRecord> patients = Collections.emptyList();

    void onStart(@Observes StartupEvent ev) {
        this.patients = loadAllFiles();
        Log.infof("Loaded %d patient records from %s", patients.size(), rosterPath);
    }


    public List<PatientRecord> getPatients() {
        return patients;
    }


    private List<PatientRecord> loadAllFiles() {
        Path dir = Paths.get(rosterPath);

        if (!Files.isDirectory(dir)) {
            Log.warnf("Roster path does not exist or is not a directory: %s", rosterPath);
            return Collections.emptyList();
        }

        List<PatientRecord> result = new ArrayList<>();
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(this::isDicomFile)
                    .forEach(path -> {
                        try {
                            PatientRecord patientRecord = parseDicomFile(path);
                            result.add(patientRecord);
                        } catch (IOException e) {
                            Log.errorf("Failed to parse DICOM file %s: %s", path, e.getMessage());
                        }
                    });
        } catch (IOException e) {
            Log.errorf("Cannot read roster directory %s: %s", rosterPath, e.getMessage());
        }
        return Collections.unmodifiableList(result);
    }

    private boolean isDicomFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return false;
        }
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".dcm");
    }

    private PatientRecord parseDicomFile(Path path) throws IOException {
        try (DicomInputStream dis = new DicomInputStream(path.toFile())) {
            dis.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
            Attributes attrs = dis.readDataset();

            String patientId = attrs.getString(Tag.PatientID, "").trim();
            String patientName = attrs.getString(Tag.PatientName, "").trim();

            String[] nameParts = patientName.split("\\^", -1);
            String lastName = nameParts.length > 0 ? nameParts[0].trim() : "";
            String firstName = nameParts.length > 1 ? nameParts[1].trim() : "";
            return new PatientRecord(
                    patientId,
                    lastName,
                    firstName,
                    path.getFileName().toString()
            );
        }
    }
}
