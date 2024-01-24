package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.AcademicRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.util.ResponseStructure;

public interface AcademicProgramService {

	ResponseEntity<ResponseStructure<AcademicProgramsResponse>> addAcademicPrograms(int schoolId,AcademicRequest academicRequest);

	ResponseEntity<ResponseStructure<List<AcademicProgramsResponse>>> findAllAcademicProgram(int schoolId);


}
