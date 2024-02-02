package com.school.sba.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService  {

	ResponseEntity<ResponseStructure<String>> generateClassHourForAcademicProgram(int programId);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(ArrayList<ClassHourRequest> classHours);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> deleteClassHour(List<ClassHour> classHour);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForNextWeek(int programId);
	


}
