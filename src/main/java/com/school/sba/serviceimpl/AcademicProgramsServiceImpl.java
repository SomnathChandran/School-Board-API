package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicPrograms;
import com.school.sba.entity.School;
import com.school.sba.exceptions.SchoolNotFoundException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.AcademicRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;
@Service
public class AcademicProgramsServiceImpl implements AcademicProgramService{

	
	@Autowired
	private AcademicProgramsRepository academicRepo;

	@Autowired 
	private SchoolRepository schoolRepo;
	
	

	@Autowired
	private ResponseStructure<AcademicProgramsResponse> structure;
	
	public static AcademicProgramsResponse mapToAcademicResponse(AcademicPrograms academicPrograms) {
		return AcademicProgramsResponse.builder()
				.beginsAt(academicPrograms.getBeginsAt())
				.endsAt(academicPrograms.getEndsAt())
				.programId(academicPrograms.getProgramId())
				.programName(academicPrograms.getProgramName())
				.programType(academicPrograms.getProgramType())
				.build();
	}
	private AcademicPrograms mapToAcademicRequest(AcademicRequest academicRequest) {
		return AcademicPrograms.builder()
				.beginsAt(academicRequest.getBeginsAt())
				.endsAt(academicRequest.getEndsAt())
				.programName(academicRequest.getProgramName())
				.programType(academicRequest.getProgramType())
				.build();
	}
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> addAcademicPrograms(int schoolId,AcademicRequest academicRequest) {
		School school = schoolRepo.findById(schoolId).orElseThrow(()-> new SchoolNotFoundException("School Not Found Exception!!"));
		AcademicPrograms academicPrograms =mapToAcademicRequest(academicRequest);
		academicPrograms.setSchool(school);
		academicRepo.save(academicPrograms);
		structure.setStatus(HttpStatus.CREATED.value());
		structure.setMessage("Academic Programs Added for School Successfully!!");
		structure.setData(mapToAcademicResponse(academicPrograms));
		return new ResponseEntity<ResponseStructure<AcademicProgramsResponse>>(structure,HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramsResponse>>>findAllAcademicProgram(int schoolId) {
		return schoolRepo.findById(schoolId).map(school->{
			List<AcademicPrograms> academicPrograms = school.getAcagemicPrograms();
			List<AcademicProgramsResponse> li=new ArrayList<>();
			ResponseStructure<List<AcademicProgramsResponse>> rs=new ResponseStructure<>();
			for(AcademicPrograms al:academicPrograms)
			{
				li.add(mapToAcademicResponse(al));
			}
			
			rs.setStatus(HttpStatus.FOUND.value());
			rs.setMessage("Academic Program Found Successfully");
			rs.setData(li);
			
			return new ResponseEntity<ResponseStructure<List<AcademicProgramsResponse>>>(rs,HttpStatus.FOUND);
		}).orElseThrow(()-> new SchoolNotFoundException("Requested School Not Found"));
			
		}

}
