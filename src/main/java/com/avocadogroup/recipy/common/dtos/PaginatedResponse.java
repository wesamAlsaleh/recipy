package com.avocadogroup.recipy.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content; // List of something `T`
    private int pageNumber; // MetaData
    private int pageSize; // MetaData
    private long totalElements; // MetaData
    private int totalPages; // MetaData
    private boolean last; // MetaData
}
