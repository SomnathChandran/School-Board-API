package com.school.sba.requestdto;

import org.springframework.stereotype.Component;

import com.school.sba.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Component
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

	@NotBlank(message = "USERNAME IS MANDATORY")
	private String userName;
	@Size(min = 8,max = 20,message = "pasword must be 8 to 20 character")
	@Pattern(regexp = "^(?=.[a-z])(?=.[A-Z])(?=.\\d)(?=.[@#$%^&+=!])(?=\\S+$).{8,}$",
	message = "Password must have 1 uppercase, 1 lowercase, 1 number, 1 special character, and be at least 8 characters long")
	private String password;
	@NotBlank(message = "FirstName is mandatory")
	private String firstName;
	private String lastName;
	private long contactNo;
	@Email(regexp = "[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", message = "invalid email ")
	@NotBlank(message = "email canot be blank")
	private String email;
	@NotNull(message = "User Role is mandatory")
	private UserRole userRole;
}
