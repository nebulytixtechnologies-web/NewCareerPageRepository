package com.neb.repo;



import com.neb.entity.DeveloperApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<DeveloperApplication, Long> {
    boolean existsByEmail(String email);
}
