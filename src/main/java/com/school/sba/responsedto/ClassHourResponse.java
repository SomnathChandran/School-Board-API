package com.school.sba.responsedto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.school.sba.entity.Subject;
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
public class ClassHourResponse {
	
	private int classHourId;
	private LocalDateTime beginsAt;
	private LocalDateTime endsAt;
	private int roomNo;
	private ClassStatus classStatus; 
	private Subject subject;

}
