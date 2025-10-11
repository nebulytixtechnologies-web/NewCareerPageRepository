package com.neb.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class DeveloperRequest {
	private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String qualification;
    private Integer passoutYear;
    private String internship;
    private String domain;
    private String companyName;
    private Double salary;
    private Double duration;
    private MultipartFile resume;
    private String code;
    public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public Integer getPassoutYear() {
		return passoutYear;
	}
	public void setPassoutYear(Integer passoutYear) {
		this.passoutYear = passoutYear;
	}
	public String getInternship() {
		return internship;
	}
	public void setInternship(String internship) {
		this.internship = internship;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public Double getDuration() {
		return duration;
	}
	public void setDuration(Double duration) {
		this.duration = duration;
	}
	public MultipartFile getResume() {
		return resume;
	}
	public void setResume(MultipartFile resume) {
		this.resume = resume;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public DeveloperRequest(String role, String firstName, String lastName, String email, String phone,
			String qualification, Integer passoutYear, String internship, String domain, String companyName,
			Double salary, Double duration, MultipartFile resume, String code) {
		super();
		this.role = role;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.qualification = qualification;
		this.passoutYear = passoutYear;
		this.internship = internship;
		this.domain = domain;
		this.companyName = companyName;
		this.salary = salary;
		this.duration = duration;
		this.resume = resume;
		this.code = code;
	}
	
}
