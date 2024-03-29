package com.school.sba.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SchoolNotFoundException extends RuntimeException {
	private String message;
}
