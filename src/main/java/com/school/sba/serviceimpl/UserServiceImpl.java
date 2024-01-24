package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptions.AdminOnlyException;
import com.school.sba.exceptions.ContraintsValidationException;
import com.school.sba.exceptions.DuplicateEntryException;
import com.school.sba.exceptions.ExistingAdminException;
import com.school.sba.exceptions.UserNotFoundByIdException;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private ResponseStructure<UserResponse> structure;

	static boolean admin = false;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private PasswordEncoder encoder;

	private User mapToUser(UserRequest request) {

		return User.builder().email(request.getEmail()).contactNo(request.getContactNo())
				.firstName(request.getFirstName()).lastName(request.getLastName())
				.password(encoder.encode(request.getPassword()))
				.userName(request.getUserName()).userRole(request.getUserRole()).isDelete(false).build();
	}

	private UserResponse mapToUserResponse(User user) {
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
		return new ResponseEntity<>(structure, HttpStatus.OK);
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
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest user) {
		User user1 = userRepo.save(mapToUser(user));
		structure.setData(mapToUserResponse(user1));
		structure.setMessage("User Saved Successfully");
		structure.setStatus(HttpStatus.CREATED.value());
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);
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





}


