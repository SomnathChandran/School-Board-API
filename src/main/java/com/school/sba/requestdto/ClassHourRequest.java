package com.school.sba.requestdto;

import java.time.LocalTime;

import com.school.sba.enums.ClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassHourRequest {

	private LocalTime beginsAt;
	private LocalTime endsAt;
	private int roomNo;
	private ClassStatus classStatus; 
	private int subjectId;
	private int userId;
	private int classHourId;

}
