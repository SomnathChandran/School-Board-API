package com.school.sba.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleNotFoundBySchoolIdException extends RuntimeException {
	private String message;
	

}
