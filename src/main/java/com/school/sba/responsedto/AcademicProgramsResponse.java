package com.school.sba.responsedto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.school.sba.enums.ProgramType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcademicProgramsResponse {
	private int programId;
	private ProgramType programType;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;

}
