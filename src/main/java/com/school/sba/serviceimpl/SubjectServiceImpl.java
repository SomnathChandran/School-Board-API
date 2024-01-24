package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
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
	private AcademicProgramsServiceImpl academicProgramsServiceImpl;

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> addSubjectToProgram(int programId, SubjectRequest subjectRequest) {

		return academicRepo.findById(programId).map(program ->{
			List<Subject> subjects=new ArrayList<Subject>();
			subjectRequest.getSubjects().forEach(name-> {

				Subject subject=subjectRepo.findBySubjectName(name).map(s->s).orElseGet(()->{
					Subject subject2=new Subject();
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
			structure.setData(academicProgramsServiceImpl. mapToAcademicResponse(program));
			return new ResponseEntity<ResponseStructure<AcademicProgramsResponse>>(structure,HttpStatus.CREATED);

		}).orElseThrow(()->new AcademicProgramNotFoundByIdException("Academic program Not found for given id"));

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
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> findAllSubjects() {
		// TODO Auto-generated method stub
		return null;
	}
	}