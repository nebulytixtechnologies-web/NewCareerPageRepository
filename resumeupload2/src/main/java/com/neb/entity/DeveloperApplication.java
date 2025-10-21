package com.neb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
/**
 * Entity class representing a developer's job application.
 * Maps to a database table (name will default to 'developer_application' if not specified).
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperApplication {
	/** Primary key: auto-generated unique ID for each application. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private int passoutYear;
    private boolean internship;
    private String domain;
    private String companyName;
    private String devdomain;
    private double salary;
    private double duration;
    private String resumePath;
    private Instant createdAt;
    private String gender;
}
