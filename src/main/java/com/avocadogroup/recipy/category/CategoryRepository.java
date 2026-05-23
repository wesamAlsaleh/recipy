package com.avocadogroup.recipy.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDeletedTrue();

    List<Category> findByDeletedFalse();

    Optional<Category> findByName(String name);
}
