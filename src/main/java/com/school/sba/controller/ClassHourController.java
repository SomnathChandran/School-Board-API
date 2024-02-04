package com.school.sba.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseStructure;

@RestController
public class ClassHourController {

	@Autowired
	private ClassHourService classHourService;

	@PostMapping("/academic-program/{programId}/class-hours")
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForAcademicProgram(@PathVariable int programId){
		return classHourService.generateClassHourForAcademicProgram(programId);
	}

	@PutMapping("/class-hours")
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(@RequestBody ArrayList<ClassHourRequest>classHours){
		return classHourService.updateClassHour(classHours);
	}
	@PostMapping("/academic-program/{programId}")
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForNextWeek(int programId){
		return classHourService.generateClassHourForNextWeek(programId);
		
	}
	
	@PostMapping("/academic-programs/{programId}/class-hours/write-excel")
	public ResponseEntity<ResponseStructure<String>>generateClassHourInExcel(@PathVariable int programId,@RequestBody ExcelRequestDto excelRequestDto){
		return classHourService.generateClassHourInExcel(programId,excelRequestDto);
	}
	@PostMapping("/academic-programs/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
	public ResponseEntity<?>writeToExcel(@RequestParam MultipartFile file ,@PathVariable int programId,@PathVariable LocalDate fromDate,@PathVariable LocalDate toDate )throws IOException{
		return classHourService.writeToExcel(file,programId,fromDate,toDate);
	}

}
