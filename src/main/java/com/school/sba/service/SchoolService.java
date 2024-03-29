package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.util.ResponseStructure;

public interface SchoolService {
	
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool( SchoolRequest schoolRequest);

	public ResponseEntity<ResponseStructure<SchoolResponse>> deleteSchool(int schoolId);
	
	void permanentDeleteSchool();
}
