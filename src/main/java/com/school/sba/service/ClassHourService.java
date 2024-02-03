package com.school.sba.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService  {

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForAcademicProgram(int programId);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(ArrayList<ClassHourRequest> classHours);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> deleteClassHour(List<ClassHour> classHour);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForNextWeek(int programId);

	ResponseEntity<ResponseStructure<String>> generateClassHourInExcel(int programId,ExcelRequestDto excelRequestDto);

	


}
