package com.neb.entity;
//this is entity Class
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "career_applications")
@Data
@NoArgsConstructor
public class CareerApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String phone;
    private String qualification;
    //add experience for developers
    private int passoutYear;
    private String domain;
    private String resumeFileName;
    private Instant appliedAt;
    private String gender;
   

  }
