package com.school.sba.entity;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class School {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int schoolId;
	private String schoolName;
	private long schoolcontactNo;
	private String schoolMail;
	private String schoolAddress;
	private boolean isDelete;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Schedule schedule;
	
	@OneToMany(mappedBy = "school")
	private List<AcademicPrograms> academicPrograms = new ArrayList<AcademicPrograms>();
	
}
