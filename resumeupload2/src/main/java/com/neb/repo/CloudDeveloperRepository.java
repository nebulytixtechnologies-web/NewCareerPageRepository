package com.neb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neb.entity.CloudDeveloper;
/**
 * Repository for CloudDeveloper entity.
 * Provides basic CRUD operations and a custom method to check email existence.
 * It extended from the JPA Repository.
 */
@Repository
public interface CloudDeveloperRepository extends JpaRepository<CloudDeveloper, Long> {
    
    /** Check if a Cloud Developer already exists by email */
    boolean existsByEmail(String email);
    
}
