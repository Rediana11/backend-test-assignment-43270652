# Technical Decisions

### Roster Loading Strategy
- Patient records are loaded once at application startup via a CDI `@Observes StartupEvent` listener in `RosterLoader`.
- All DICOM files (*.dcm) are read from the configured roster directory and parsed into `PatientRecord` objects held in memory.
- This approach is simple and performant for the context and given dataset size (13 files). For a production scenario with a large or dynamic roster, a scheduled reload or file-watcher could be added without restarting the service.

### DICOM Parsing
- Used `DicomInputStream` from dcm4che3 (already on the classpath) to read DICOM metadata.
- `IncludeBulkData.NO` is set to skip pixel data and other large binary content — only header tags are needed for patient identification, which makes parsing fast and memory-efficient.

### Matching Logic
- **PatientID**: exact match, case-insensitive.
- **Last name**: prefix match, case-insensitive. This allows partial searches (e.g., "Mül" matches "Müller").
- **Both criteria**: results must satisfy both filters (AND logic).
- At least one search parameter must be provided; otherwise a 400 error is returned.

## Error Handling

### Global Exception Handler
- A single `@Provider` `ExceptionMapper<Exception>` handles all exceptions centrally.
- Custom exceptions like `MissingSearchCriteriaException` extend `BadRequestException`, which allows the handler to map them to the correct HTTP status without explicit per-exception checks. This pattern makes it easy to add new domain-specific exceptions (e.g., `InvalidPageException`) — they just extend the appropriate JAX-RS exception class and are automatically handled.
- All error responses use a consistent `ErrorResponse` structure with `status`, `error`, `message`, and `timestamp` fields, ensuring clients always receive a predictable error format regardless of the failure type.

## Testing Strategy

Both unit and integration tests were chosen because they serve complementary purposes:
- **Unit tests** validate the search logic in isolation, are fast to execute, and make it easy to cover edge cases (case-insensitivity, prefix matching, null handling) without the overhead of starting the full application.
- **Integration tests** prove the system works end-to-end — from HTTP request through CDI injection, DICOM file parsing, and JSON serialization. They catch issues that unit tests cannot, such as misconfigured endpoints, serialization problems, or CDI wiring errors.
- Used the data in test-data/roster for the integration tests, since we have read only, no modification on the files.
Implemented both since they provide confidence that both the business logic is correct and the application behaves as expected when deployed.


