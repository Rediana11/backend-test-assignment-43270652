package com.telepaxx.assignment.model;

import java.util.List;

/**
 * Wraps search results with pagination metadata.
 */
public record PaginatedResponse<T>(
        List<T> data,
        int page,
        int pageSize,
        int totalResults,
        int totalPages
) {
    public static <T> PaginatedResponse<T> of(List<T> allResults, int page, int pageSize) {
        int totalResults = allResults.size();
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalResults);

        List<T> pageData = (fromIndex >= totalResults)
                ? List.of()
                : allResults.subList(fromIndex, toIndex);

        return new PaginatedResponse<>(pageData, page, pageSize, totalResults, totalPages);
    }
}
