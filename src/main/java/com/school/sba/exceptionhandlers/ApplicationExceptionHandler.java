package com.school.sba.exceptionhandlers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptions.AdminOnlyException;
import com.school.sba.exceptions.ContraintsValidationException;
import com.school.sba.exceptions.DuplicateEntryException;
import com.school.sba.exceptions.ExistingAdminException;
import com.school.sba.exceptions.IllegalRequestException;
import com.school.sba.exceptions.ScheduleNotFoundException;
import com.school.sba.exceptions.SchoolNotFoundException;
import com.school.sba.exceptions.UserNotFoundByIdException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	public ResponseEntity<Object> structre(HttpStatus status, String message, Object rootCause) {
		return new ResponseEntity<Object>(
				Map.of(
				"status", status.value(),
				"message", message,
				"rootcause", rootCause),HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ContraintsValidationException.class)
	public ResponseEntity<Object> contraintsValidationException(ContraintsValidationException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "no duplicate values for phone number and email ");
	}

	@ExceptionHandler(ExistingAdminException.class)
	public ResponseEntity<Object> existingAdminException(ExistingAdminException ex) {
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(), "Admin already present no duplicate admin ");
	}
	
	@ExceptionHandler(UserNotFoundByIdException.class)
	public ResponseEntity<Object> userNotFoundByIdException(UserNotFoundByIdException ex) {
		return structre(HttpStatus.NOT_FOUND, ex.getMessage(), "user not found with the specified user ID  ");
	}
	
	@ExceptionHandler(IllegalRequestException.class)
	public ResponseEntity<Object> illegalRequestException(IllegalRequestException ex){
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(),"Invalid Request!!");
	}
	@ExceptionHandler(SchoolNotFoundException.class)
	public ResponseEntity<Object> schoolNotFoundException(SchoolNotFoundException ex){
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(),"Invalid Request!!");
	}
	@ExceptionHandler(DuplicateEntryException.class)
	public ResponseEntity<Object> duplicateEntryException(DuplicateEntryException ex){
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(),"Invalid Request!!");
	}
	@ExceptionHandler(ScheduleNotFoundException.class)
	public ResponseEntity<Object> scheduleNotFoundException(ScheduleNotFoundException ex){
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(),"Invalid Request!!");
	}
	@ExceptionHandler(AcademicProgramNotFoundByIdException.class)
	public ResponseEntity<Object> academicProgramNotFoundByIdException(AcademicProgramNotFoundByIdException ex){
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(),"Invalid Request!!");
	}
	@ExceptionHandler(AdminOnlyException.class)
	public ResponseEntity<Object> adminOnlyException(AdminOnlyException ex){
		return structre(HttpStatus.BAD_REQUEST, ex.getMessage(),"Invalid Request!!");
	}
}
