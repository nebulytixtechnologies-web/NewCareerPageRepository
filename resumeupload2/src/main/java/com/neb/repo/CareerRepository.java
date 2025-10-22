package com.neb.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.neb.entity.CareerApplication;

/**
 * Repository for CareerApplication entity.
 * Provides basic CRUD operations and custom methods for email lookup.
 * It extended from the JPA Repository.
 */
public interface CareerRepository extends JpaRepository<CareerApplication, Long>
{
	/** Checks if a career application exists for the given email */
	boolean existsByEmail(String email);
	/** Finds a career application by email */
	Optional<CareerApplication> findByEmail(String email);
}

