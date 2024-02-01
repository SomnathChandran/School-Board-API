package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.controller.AcademicProgramsController;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.util.ResponseStructure;

public interface SubjectService {

	ResponseEntity<ResponseStructure<AcademicProgramsResponse>> addSubjectToProgram(int programId,SubjectRequest subjectRequest);

	ResponseEntity<ResponseStructure<AcademicProgramsResponse>> updateSubjectProgram(int programId, SubjectRequest subjectRequest);

	ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects();

	ResponseEntity<ResponseStructure<SubjectResponse>> deleteSubject(int subjectId);

}
