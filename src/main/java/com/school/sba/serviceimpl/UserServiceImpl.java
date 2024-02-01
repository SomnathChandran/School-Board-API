package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptions.AdminOnlyException;
import com.school.sba.exceptions.ContraintsValidationException;
import com.school.sba.exceptions.DuplicateEntryException;
import com.school.sba.exceptions.ExistingAdminException;
import com.school.sba.exceptions.IllegalRequestException;
import com.school.sba.exceptions.InvalidUserRoleException;
import com.school.sba.exceptions.SchoolNotFoundException;
import com.school.sba.exceptions.SubjectNotFoundExceptionByID;
import com.school.sba.exceptions.SubjectsOnlyAddedToTeacherException;
import com.school.sba.exceptions.UserIsNotAnAdminException;
import com.school.sba.exceptions.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private ResponseStructure<UserResponse> structure;
	@Autowired
	private ResponseStructure<List<UserResponse>> structureList;

	static boolean admin = false;

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private AcademicProgramsRepository academicRepo;

	@Autowired
	private PasswordEncoder encoder;

	private User mapToUser(UserRequest request) {

		return User.builder().email(request.getEmail()).contactNo(request.getContactNo())
				.firstName(request.getFirstName()).lastName(request.getLastName())
				.password(encoder.encode(request.getPassword()))
				.userName(request.getUserName()).userRole(request.getUserRole()).isDelete(false).build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().contactNo(user.getContactNo()).email(user.getEmail())
				.firstName(user.getFirstName()).lastName(user.getLastName()).userId(user.getUserId())
				.userName(user.getUserName()).userRole(user.getUserRole()).build();
	}

	public ResponseEntity<ResponseStructure<UserResponse>> getStructure(HttpStatus status, String message, Object data) {
		structure.setData(mapToUserResponse((User) data));
		structure.setMessage("user added successfully ");
		structure.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK);
	}


	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> findUserById(int userId) {
		User user = userRepo.findById(userId).map(user2 -> {
			return user2;
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found "));
		structure.setData(mapToUserResponse(user));
		structure.setMessage("USER FOUND ");
		structure.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteByUserId(int userId) {
		User user1 = userRepo.findById(userId).map(user -> {
			user.setDelete(true);
			return user;
		}).orElseThrow(() -> new UserNotFoundByIdException("user not found by ID "));
		userRepo.save(user1);
		structure.setData(mapToUserResponse(user1));
		structure.setMessage("user Deleted !! ");
		structure.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest) {
		String authenticatedName = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUserName(authenticatedName).map(user -> {
			if(user.getSchool() != null) {
				if(userRequest.getUserRole()!= UserRole.ADMIN) {
					User user1 = userRepo.save(mapToUser(userRequest));
					user1.setSchool(user.getSchool());
					structure.setData(mapToUserResponse(user1));
					structure.setMessage("User Saved Successfully");
					structure.setStatus(HttpStatus.CREATED.value());
				}
				else{
					throw new ExistingAdminException("ADMIn Already Presented!!");
				}
			}else{
				throw new SchoolNotFoundException("School Not Found For The Respected User!!");
			}
			return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);
		}).orElseThrow(()-> new UserIsNotAnAdminException("only User Can DO This Task!!"));
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addAdmin(UserRequest request) {
		if(request.getUserRole()==UserRole.ADMIN)
		{
			if(userRepo.existsByUserRole(request.getUserRole())==false)
			{
				System.out.println("hi");
				User user1 = userRepo.save(mapToUser(request));
				structure.setData(mapToUserResponse(user1));
				structure.setMessage("Admin Created Successfully");
				structure.setStatus(HttpStatus.CREATED.value());
				return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);
			}
			else
			{
				throw new ExistingAdminException("Admin can be only one Person");
			}
		}
		else {
			throw new AdminOnlyException("Only Admin Should Be Add");	
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int subjectId, int userId) {
		return userRepo.findById(userId).map(user -> {
			System.out.println(user.getUserRole());
			if (user.getUserRole().toString().equals("TEACHER")) {
				System.out.println(user.getUserRole());

				subjectRepo.findById(subjectId).map(subject -> {
					user.setSubject(subject);
					userRepo.save(user);
					return user;
				}).orElseThrow(() -> new SubjectNotFoundExceptionByID("invalid Id "));
			} else {
				throw new SubjectsOnlyAddedToTeacherException("subjects only added to Teachers");
			}
			structure.setData(mapToUserResponse(user));
			structure.setMessage(" Subject successfully added to Teacher ");
			structure.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.OK);
		}).orElseThrow(() -> new UserNotFoundByIdException("invalid Id "));	
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findUserDetailsByRole(UserRole role,int programId) {
		List<UserResponse>uList = new ArrayList<UserResponse>();
		return academicRepo.findById(programId).map(program->{
			if(program.getUsers().size()!=0) {
				program.getUsers().forEach(users ->{
					if(role!= UserRole.ADMIN) {
						if(role.equals(users.getUserRole())) {
								uList.add(mapToUserResponse(users));
							}
						else {
							throw new InvalidUserRoleException("The Given User Role Must Be Same As the Existing Role!!");
						}
					} else {
						throw new InvalidUserRoleException("The Given User Role Must Be TEACHER OR STUDENT!!");
					}
				});
				structureList.setStatus(HttpStatus.FOUND.value());
				structureList.setMessage("The List Of User Details Fetched SuccessFully");
				structureList.setData(uList);
				return new ResponseEntity<ResponseStructure<List<UserResponse>>>(structureList,HttpStatus.FOUND);
			}
			else {
				throw new IllegalRequestException("Program Should Have List Of Users!!");
			}
		}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("Ivalid Academic Program ID!!"));
		
	}





}


