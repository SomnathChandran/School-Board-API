package com.school.sba.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectNotFoundExceptionByID extends RuntimeException{
	
	private String message;

}
