package com.neb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.CareerApplication;

public interface CareerRepository extends JpaRepository<CareerApplication, Long>
{
	boolean existsByEmail(String email);
	Optional<CareerApplication> findByEmail(String email);
}

