package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptions.SubjectNotFoundExceptionByID;
import com.school.sba.exceptions.SubjectNotPresentException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;
@Service
public class SubjectServiceImpl implements SubjectService{

	@Autowired
	private AcademicProgramsRepository academicRepo;
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private ResponseStructure<AcademicProgramsResponse> structure ;
	@Autowired
	private ResponseStructure<SubjectResponse> structureSub ;
	@Autowired
	private AcademicProgramsServiceImpl academicProgramsServiceImpl;

	public SubjectResponse mapToSubjectResponse(Subject subject) {
		return SubjectResponse.builder()
				.subjectId(subject.getSubjectId())
				.subjectName(subject.getSubjectName())
				.build();
	}


	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> addSubjectToProgram(int programId, SubjectRequest subjectRequest) {
			return academicRepo.findById(programId).map(program -> {
				List<Subject> subjects = new ArrayList<Subject>();
				System.out.println("enter");
				subjectRequest.getSubjects().forEach(name -> {
					Subject subject = subjectRepo.findBySubjectName(name).map(s -> s).orElseGet(() -> {
						Subject subject2 = new Subject();
						subject2.setSubjectName(name);
						subjectRepo.save(subject2);
						return subject2;
					});
					subjects.add(subject);
				});
				program.setSubjects(subjects);
				academicRepo.save(program);
				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMessage("Updated the Subject list to Academic Program");
				structure.setData(academicProgramsServiceImpl.mapToAcademicResponse(program));
				return new ResponseEntity<ResponseStructure<AcademicProgramsResponse>>(structure, HttpStatus.CREATED);
			}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Academic program Not found for given id"));
		}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> updateSubjectProgram(int programId,
			SubjectRequest subjectRequest) {

		return academicRepo.findById(programId).map(program -> {
			// Remove the existing subjects for the program
			program.getSubjects().clear();
			academicRepo.save(program);

			return addSubjectToProgram(programId,subjectRequest);

		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Academic program Not found for given id"));

	}


	@Override
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects() {
		List<Subject> list = subjectRepo.findAll();
		if(list.size()!=0) {
			List<SubjectResponse> subjects = new ArrayList<>();
			list.forEach(subject -> {
				subjects.add(mapToSubjectResponse(subject));
			});
			ResponseStructure<List<SubjectResponse>> rs = new ResponseStructure<>();
			rs.setData(subjects);
			rs.setMessage("List Of Subjects Fetched Succesfully");
			rs.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(rs, HttpStatus.OK);
		}else {
			throw new SubjectNotPresentException("Need To Add The Subjects First..");
		}
	}
	
	public ResponseEntity<ResponseStructure<SubjectResponse>> deleteSubject(int subjectId) {
		return subjectRepo.findById(subjectId).map(subject ->{
			if(subject.isDelete()== false) {
				subject.setDelete(true);
				subjectRepo.save(subject);
				structureSub.setStatus(HttpStatus.OK.value());
				structureSub.setMessage("The Subject Successfully Is Deleted !!");
				structureSub.setData(mapToSubjectResponse(subject));
			}
			return new ResponseEntity<ResponseStructure<SubjectResponse>>(structureSub,HttpStatus.OK);
		}).orElseThrow(()-> new SubjectNotFoundExceptionByID("The Expected Subject Is Not Found By Given Id!!"));
		
	}
}