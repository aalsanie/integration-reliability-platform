package io.github.aalsanie.irp.common.api;

import java.util.List;

public record PageResponse<T>(List<T> items,
                              int page,
                              int size,
                              long totalElements,
                              int totalPages,
                              boolean first,
                              boolean last) {
}