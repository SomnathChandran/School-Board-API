package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.util.ResponseStructure;


public interface UserService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser( UserRequest user);

	ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> deleteByUserId(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> addAdmin(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int subjectId, int userId);
	
	ResponseEntity<ResponseStructure<List<UserResponse>>> findUserDetailsByRole(UserRole role, int programId);
	
	void permanentDeleteUser();


}
