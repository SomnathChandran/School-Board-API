package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exceptions.ContraintsValidationException;
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

	@Autowired
	private UserRepository userRepo;
	
	private User mapToUser(UserRequest request) {

		return User.builder().email(request.getEmail()).contactNo(request.getContactNo())
				.firstName(request.getFirstName()).lastName(request.getLastName()).password(request.getPassword())
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
	public ResponseEntity<ResponseStructure<UserResponse>> regesterUser(UserRequest user) {
		try {
			if (user.getUserRole() == UserRole.ADMIN) {
				if (userRepo.existsByUserRole(user.getUserRole()) != true) {
					User userEntity = userRepo.save(mapToUser(user));

					return new ResponseEntity<ResponseStructure<UserResponse>>(
							new ResponseStructure<UserResponse>(HttpStatus.OK.value(), "user added successfully ",
									mapToUserResponse(userEntity)),
							HttpStatus.OK);
				} else {
					throw new ExistingAdminException("ADMIN alredy existed ");
				}
			}
		} catch (Exception e) {
			throw new ContraintsValidationException("no duplicate values");
		}
		User userEntity = userRepo.save(mapToUser(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(new ResponseStructure<UserResponse>(
				HttpStatus.OK.value(), "user added successfully ", mapToUserResponse(userEntity)), HttpStatus.CREATED);

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

}
