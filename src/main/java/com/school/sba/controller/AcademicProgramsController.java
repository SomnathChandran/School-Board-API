package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.AcademicRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@RestController
public class AcademicProgramsController {
	
	@Autowired
	private AcademicProgramService academicProgramService;
	
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>>addAcademicPrograms(@PathVariable int schoolId, @RequestBody AcademicRequest academicRequest ) {
		 return academicProgramService.addAcademicPrograms(schoolId,academicRequest);
	}
	
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramsResponse>>>findAllAcademicProgram(@PathVariable int schoolId){
		return academicProgramService.findAllAcademicProgram(schoolId);
	}
	

}
