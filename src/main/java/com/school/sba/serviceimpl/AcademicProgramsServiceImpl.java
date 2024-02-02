package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicPrograms;
import com.school.sba.entity.School;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptions.AdminCannotBeAssignedToAcademicException;
import com.school.sba.exceptions.DuplicateEntryException;
import com.school.sba.exceptions.IllegalRequestException;
import com.school.sba.exceptions.InvalidUserRoleException;
import com.school.sba.exceptions.IrreleventTeacherException;
import com.school.sba.exceptions.SchoolNotFoundException;
import com.school.sba.exceptions.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.AcademicRequest;
import com.school.sba.responsedto.AcademicProgramsResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;
@Service
public class AcademicProgramsServiceImpl implements AcademicProgramService{


	@Autowired
	private AcademicProgramsRepository academicRepo;

	@Autowired 
	private SchoolRepository schoolRepo;

	@Autowired 
	private UserRepository userRepo;
	@Autowired 
	private ClassHourRepository classHourRepo;


	@Autowired
	private ResponseStructure<AcademicProgramsResponse> structure;

	public  AcademicProgramsResponse mapToAcademicResponse(AcademicPrograms academicPrograms) {
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
			List<AcademicPrograms> academicPrograms = school.getAcademicPrograms();
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
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> assignTeacherAndStudent(int programId,int userId) {
		return userRepo.findById(userId).map(user->{
			return academicRepo.findById(programId).map(program->{

				if(user.getUserRole()!=UserRole.ADMIN)
				{
					if(user.getUserRole()==UserRole.STUDENT)
					{
						user.getPrograms().add(program);
						userRepo.save(user);
						program.getUsers().add(user);
						academicRepo.save(program);
						structure.setStatus(HttpStatus.CREATED.value());
						structure.setMessage(user.getUserRole()+" is Added to academic Program");
						structure.setData(mapToAcademicResponse(program));
					}
					if(user.getUserRole()==UserRole.TEACHER && !program.getUsers().contains(user) )
					{
						if( program.getSubjects().contains(user.getSubject())) 
						{
							program.getUsers().add(user);
							academicRepo.save(program);
						}
						else {
							throw new IrreleventTeacherException("The Subject of the Teacher is irreveleant to academic program subject");
						}
						structure.setStatus(HttpStatus.CREATED.value());
						structure.setMessage(user.getUserRole()+" added is Added to academic Program");
						structure.setData(mapToAcademicResponse(program));
					}
					else
					{
						throw new DuplicateEntryException(" duplicate user will not be allowed  to the same Academic Program ");
					}
					return new ResponseEntity<ResponseStructure<AcademicProgramsResponse>>(structure,HttpStatus.CREATED);
				}
				else
					throw new AdminCannotBeAssignedToAcademicException("Admin cannot assigned to the Academic program");
			}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("Academic Program is not found in given ID"));
		}).orElseThrow(()->new UserNotFoundByIdException("User is not found in the given ID"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramsResponse>> deleteAcademicProgram(int programId) {
		return academicRepo.findById(programId).map(program ->{
			if(program.isDelete()== false) {
				program.setDelete(true);
				academicRepo.save(program);
				structure.setStatus(HttpStatus.OK.value());
				structure.setMessage("The Academic Program Is Deleted !!");
				structure.setData(mapToAcademicResponse(program));
			}
			return new ResponseEntity<ResponseStructure<AcademicProgramsResponse>>(structure,HttpStatus.OK);
		}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("The Expected Academic Program Is Not Found By Given Id!!"));
		
	}
	@Override
	public void permanentDeleteAcademicProgram() {
		List<AcademicPrograms> programs = academicRepo.findByIsDelete(true);
		programs.forEach(program ->{ classHourRepo.deleteAll(program.getHours());
		academicRepo.delete(program);
		});
	}
}
