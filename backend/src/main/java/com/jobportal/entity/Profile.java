package com.jobportal.entity;

import com.jobportal.dto.Certification;
import com.jobportal.dto.Experience;
import com.jobportal.dto.ProfileDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Profile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private String jobTitle;
	private String company;
	private String location;
	private String about;
	private byte[] picture; 
	private Long totalExp;
	private List<String> skills;
	@ElementCollection
	@CollectionTable(name = "profile_experiences", joinColumns = @JoinColumn(name = "profile_id"))
	private List<Experience> experiences;

	@ElementCollection
	@CollectionTable(name = "profile_certifications", joinColumns = @JoinColumn(name = "profile_id"))
	private List<Certification> certifications;
	private List<Long>savedJobs;
	
	public ProfileDTO toDTO() {
		return new ProfileDTO(this.id, this.name, this.email, this.jobTitle, this.company, this.location, this.about, this.picture!=null?Base64.getEncoder().encodeToString(this.picture):null, this.totalExp, this.skills, this.experiences, this.certifications, this.savedJobs);
	}
}
