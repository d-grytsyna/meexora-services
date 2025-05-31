package com.meexora.eventservice.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int totalPages,
        int totalElements,
        int number,
        int size
) {
    public static <T> PaginatedResponse<T> fromPage(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getTotalPages(),
                (int) page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }
}
