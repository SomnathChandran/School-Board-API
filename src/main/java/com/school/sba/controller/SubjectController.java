package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

@RestController
public class SubjectController {

	@Autowired
	private SubjectService subjectService;

	@PostMapping("/academic-programs/{programId}/subjects")
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> addSubjectToProgram(@PathVariable int programId,@RequestBody SubjectRequest subjectRequest){
		return subjectService.addSubjectToProgram(programId, subjectRequest);
	}

	@PutMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> updateSubjectProgram(@PathVariable int programId, @RequestBody SubjectRequest subjectRequest) {
		return subjectService.updateSubjectProgram(programId,subjectRequest);
	}
	@GetMapping("/subjects")
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>>findAllSubjects(){
		return  subjectService.findAllSubjects();
	}
	@DeleteMapping("/subject/{subjectId}")
	public ResponseEntity<ResponseStructure<SubjectResponse>> deleteSubject(@PathVariable int subjectId ){
		return subjectService.deleteSubject(subjectId);
	}
	

}
