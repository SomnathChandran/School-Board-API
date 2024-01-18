package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@RestController
public class SchoolController {
	@Autowired
	private SchoolService service;

	@PostMapping("/users/{userId}/schools")
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(@PathVariable int userId,@RequestBody SchoolRequest schoolRequest){
		System.out.println("hi");
		return service.saveSchool(userId,schoolRequest);
		
	}

}