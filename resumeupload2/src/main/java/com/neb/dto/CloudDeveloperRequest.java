package com.neb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class CloudDeveloperRequest {

    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private Integer passoutYear;
    private Integer experienceYears;
    private String domain;
    private String companyName;
    private Double currentSalary;
    private Double expectedSalary;
    private Double durationAtCompany;
    private MultipartFile resume;
    private String linkedinProfile;
    private String githubProfile;
    private String code;
    private String gender;

    // Default constructor
    public CloudDeveloperRequest() {
    }

    // Full constructor
    public CloudDeveloperRequest(String role, String firstName, String lastName, String email, String phone,
                                 String qualification, Integer passoutYear, Integer experienceYears, String domain,
                                 String companyName, Double currentSalary, Double expectedSalary, Double durationAtCompany,
                                 MultipartFile resume, String linkedinProfile, String githubProfile, String code,String gender) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.passoutYear = passoutYear;
        this.experienceYears = experienceYears;
        this.domain = domain;
        this.companyName = companyName;
        this.currentSalary = currentSalary;
        this.expectedSalary = expectedSalary;
        this.durationAtCompany = durationAtCompany;
        this.resume = resume;
        this.linkedinProfile = linkedinProfile;
        this.githubProfile = githubProfile;
        this.code = code;
        this.gender=gender;
    }

    // Getters and Setters

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Integer getPassoutYear() { return passoutYear; }
    public void setPassoutYear(Integer passoutYear) { this.passoutYear = passoutYear; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Double getCurrentSalary() { return currentSalary; }
    public void setCurrentSalary(Double currentSalary) { this.currentSalary = currentSalary; }

    public Double getExpectedSalary() { return expectedSalary; }
    public void setExpectedSalary(Double expectedSalary) { this.expectedSalary = expectedSalary; }

    public Double getDurationAtCompany() { return durationAtCompany; }
    public void setDurationAtCompany(Double durationAtCompany) { this.durationAtCompany = durationAtCompany; }

    public MultipartFile getResume() { return resume; }
    public void setResume(MultipartFile resume) { this.resume = resume; }

    public String getLinkedinProfile() { return linkedinProfile; }
    public void setLinkedinProfile(String linkedinProfile) { this.linkedinProfile = linkedinProfile; }

    public String getGithubProfile() { return githubProfile; }
    public void setGithubProfile(String githubProfile) { this.githubProfile = githubProfile; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
    
}
