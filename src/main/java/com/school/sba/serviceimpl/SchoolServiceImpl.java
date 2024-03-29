package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptions.ExistingAdminException;
import com.school.sba.exceptions.IllegalRequestException;
import com.school.sba.exceptions.SchoolNotFoundException;
import com.school.sba.exceptions.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService {
	@Autowired
	private SchoolRepository schoolRepo;

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AcademicProgramsRepository academicRepo;

	@Autowired
	private ResponseStructure<SchoolResponse> structure;
	
	private SchoolResponse mapToSchoolResponse(School school) {
		return SchoolResponse.builder()
				.schoolId(school.getSchoolId())
				.schoolName(school.getSchoolName())
				.schoolMail(school.getSchoolMail())
				.schoolcontactNo(school.getSchoolcontactNo())
				.schoolAddress(school.getSchoolAddress())
				.build();
	}

	private School mapToSchool(SchoolRequest schoolRequest) {
		return School.builder()
				.schoolName(schoolRequest.getSchoolName())
				.schoolMail(schoolRequest.getSchoolMail())
				.schoolcontactNo(schoolRequest.getSchoolcontactNo())
				.schoolAddress(schoolRequest.getSchoolAddress())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool( SchoolRequest schoolRequest) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUserName(name).map(u ->{
			if(u.getUserRole()== (UserRole.ADMIN)) {
				if(u.getSchool()==null) {
					School school = mapToSchool(schoolRequest);
					school = schoolRepo.save(school);  // saved The New School
					u.setSchool(school);
					userRepo.save(u); // I Have Updated The User With new School
					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMessage("Successfully Created!!");
					structure.setData(mapToSchoolResponse(school));
					return new ResponseEntity<ResponseStructure<SchoolResponse>>(structure,HttpStatus.CREATED);
				}else
					 throw new IllegalRequestException("Existing Admin Request Exception!!");
			}else
				throw new ExistingAdminException("Existing Admin Request Exception!!");
			
		}).orElseThrow(()-> new UserNotFoundByIdException("Falied to find User!!"));
	}
	
	public ResponseEntity<ResponseStructure<SchoolResponse>> deleteSchool(int schoolId) {
		return schoolRepo.findById(schoolId).map(school ->{
			if(school.isDelete()== false) {
				school.setDelete(true);
				schoolRepo.save(school);
				structure.setStatus(HttpStatus.OK.value());
				structure.setMessage("The School Successfully Is Deleted !!");
				structure.setData(mapToSchoolResponse(school));
			}
			return new ResponseEntity<ResponseStructure<SchoolResponse>>(structure,HttpStatus.OK);
		}).orElseThrow(()-> new SchoolNotFoundException("The Expected School Is Not Found By Given Id!!"));
		
	}

	@Override
	public void permanentDeleteSchool() {
		schoolRepo.findAllByIsDelete(true).forEach(school ->{
			school.getAcademicPrograms().forEach(program ->{
				program.setSchool(null);
				academicRepo.save(program);
			});
			userRepo.findBySchool(school).forEach(user ->{
				user.setSchool(null);
				userRepo.save(user);
			});
			schoolRepo.delete(school);
		});
	}
	


}