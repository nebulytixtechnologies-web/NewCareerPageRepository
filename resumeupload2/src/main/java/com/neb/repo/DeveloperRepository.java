package com.neb.repo;



import com.neb.entity.DeveloperApplication;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository for DeveloperApplication entity.
 * Provides basic database operations and custom methods.
 * It extended from the JPA Repository.
 */
public interface DeveloperRepository extends JpaRepository<DeveloperApplication, Long> {
    /**Check if a developer application exists by email */
    boolean existsByEmail(String email);
}
