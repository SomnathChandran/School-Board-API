package com.school.sba.requestdto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class SchoolRequest{
	
	private String schoolName;
	private long schoolcontactNo;
	private String schoolMail;
	private String schoolAddress;
	

}
