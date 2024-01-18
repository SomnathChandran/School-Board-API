package com.school.sba.requestdto;

import java.time.LocalTime;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class ScheduleRequest {
	
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int classHoursPerDay;
	private int classHourLengthMins;
	private LocalTime breakTime;
	private int breakLengthMins;
	private LocalTime lunchTime;
	private int lunchLengthMins; 

}
