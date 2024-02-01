package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@RestController
public class UserController {
	@Autowired
	private UserService service;
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>>registerAdmin(@RequestBody UserRequest userRequest){
		return service.addAdmin(userRequest);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/users")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest user) {
		return service.registerUser(user);

	}

	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('STUDENT')OR hasAuthority('TEACHER')")
	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(@PathVariable int userId) {
		return service.findUserById(userId);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>> deleteByUserId(@PathVariable int userId) {
		return service.deleteByUserId(userId);
	}
	
	@PutMapping("/subjects/{subjectId}/users/{userId}")
	public ResponseEntity<ResponseStructure<UserResponse>>addSubjectToTeacher(@PathVariable int subjectId,@PathVariable int userId){
		return service.addSubjectToTeacher(subjectId,userId);
	}
	@GetMapping("/academic-programs/{programId}/user-roles/{role}/users")
	public ResponseEntity<ResponseStructure<List<UserResponse>>>findUserDetailsByRole(@PathVariable UserRole role ,@PathVariable int programId){
		return service.findUserDetailsByRole(role,programId);
	}
}
