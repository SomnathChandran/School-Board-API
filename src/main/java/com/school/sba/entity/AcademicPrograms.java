package com.school.sba.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.school.sba.enums.ProgramType;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AcademicPrograms {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int programId;
	private ProgramType programType;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	private boolean isDelete;
	
	@ManyToOne
	private School school;
	
	@ManyToMany
	private List<Subject> subjects;
	
	@ManyToMany(fetch = FetchType.EAGER)
	private List<User> users;
	
	@OneToMany(mappedBy = "academicProgram")
	private List<ClassHour> hours;

}
