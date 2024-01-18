package com.school.sba.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SchoolResponse {
	
	private int schoolId;
	private String schoolName;
	private long schoolcontactNo;
	private String schoolMail;
	private String schoolAddress;
	

}
