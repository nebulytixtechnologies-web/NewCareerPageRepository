package com.neb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neb.entity.CloudDeveloper;

@Repository
public interface CloudDeveloperRepository extends JpaRepository<CloudDeveloper, Long> {
    
    // Check if a Cloud Developer already exists by email
    boolean existsByEmail(String email);
    
    // Optional: You can add more query methods if needed
    // Example: Find all developers by domain
    // List<CloudDeveloper> findByDomain(String domain);
}
