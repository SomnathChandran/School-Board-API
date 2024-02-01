package com.school.sba.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class AdminCannotAddAcademicProgramException extends RuntimeException {
	private String message;

}
