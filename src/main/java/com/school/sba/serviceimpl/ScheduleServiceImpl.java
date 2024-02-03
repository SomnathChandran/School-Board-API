package com.school.sba.serviceimpl;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.exceptions.DuplicateEntryException;
import com.school.sba.exceptions.ScheduleNotFoundException;
import com.school.sba.exceptions.SchoolNotFoundException;
import com.school.sba.repository.ScheduleRepository;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.ScheduleService;
import com.school.sba.util.ResponseStructure;

@Service
public class ScheduleServiceImpl implements ScheduleService {

	@Autowired
	private ScheduleRepository scheduleRepo;

	@Autowired 
	private SchoolRepository schoolRepo;

	@Autowired
	private ResponseStructure<ScheduleResponse> structure;

	private ScheduleResponse mapToScheduleResponse(Schedule schedule) 
	{
		return ScheduleResponse.builder()
				.opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt())
				.classHoursPerDay(schedule.getClassHoursPerDay())
				.classHourLength((int)schedule.getClassHourLengthMins().toMinutes())
				.breakTime(schedule.getBreakTime())
				.breakLength((int)schedule.getBreakLengthMins().toMinutes())
				.lunchTime(schedule.getLunchTime())
				.lunchLength((int)schedule.getLunchLengthMins().toMinutes())
				.build();
	}

	private Schedule mapToSchedule(ScheduleRequest request) 
	{
		return Schedule.builder()
				.opensAt(request.getOpensAt())
				.closesAt(request.getClosesAt())
				.classHoursPerDay(request.getClassHoursPerDay())
				.classHourLengthMins(Duration.ofMinutes(request.getClassHourLengthMins()))
				.breakTime(request.getBreakTime())
				.breakLengthMins(Duration.ofMinutes(request.getBreakLengthMins()))
				.lunchTime(request.getLunchTime())
				.lunchLengthMins(Duration.ofMinutes(request.getBreakLengthMins()))
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(int schoolId,ScheduleRequest scheduleRequest) {
		School school = schoolRepo.findById(schoolId).orElseThrow(()-> new SchoolNotFoundException("School is Not Present"));
		if(school.getSchedule() == null) {
			
			Schedule schedule = scheduleRepo.save(mapToSchedule(scheduleRequest));
			school.setSchedule(schedule);
			school= schoolRepo.save(school);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("The Schedule Created Successfully!!");
			structure.setData((mapToScheduleResponse(schedule)));
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure,HttpStatus.CREATED);
		}else {
			throw new DuplicateEntryException("Schedule Already Exist");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleOfSchool(int schoolId) {
	    School school = schoolRepo.findById(schoolId).orElseThrow(()-> new SchoolNotFoundException("School is Not Present"));
	    if(school.getSchedule()!= null) {
	    	Schedule schedule = school.getSchedule();
	    	structure.setStatus(HttpStatus.OK.value());
	    	structure.setMessage("Schedule Found Successfully!!");
	    	structure.setData(mapToScheduleResponse(schedule));
	    	return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure,HttpStatus.OK);
	    }else {
			throw new ScheduleNotFoundException("Schedule Not Found!!");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId,ScheduleRequest scheduleRequest) {
		Schedule schedule = mapToSchedule(scheduleRequest);
		Schedule schedule1 = scheduleRepo.findById(scheduleId).map(s ->{
			schedule.setScheduleId(scheduleId);
			return scheduleRepo.save(schedule);
		}).orElseThrow(()-> new ScheduleNotFoundException("Schedule Not Found!!"));
		structure.setStatus(HttpStatus.OK.value());
		structure.setMessage("Schedule Updated Successfully!!");
		structure.setData(mapToScheduleResponse(schedule1));
		return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure,HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<ScheduleResponse>>>deleteSchedule(List<Schedule> schedule){
		List<ScheduleResponse>sList = new ArrayList<>();
		schedule.forEach(schedule1 ->{
			scheduleRepo.delete(schedule1);
			sList.add(mapToScheduleResponse(schedule1));
		});
		ResponseStructure<List<ScheduleResponse>>structureList = new ResponseStructure<>();
		structureList.setMessage("The Scheule SuccessFully Gets Deleted!!");
		structureList.setStatus(HttpStatus.OK.value());
		structureList.setData(sList);
		
		return new ResponseEntity<ResponseStructure<List<ScheduleResponse>>>(structureList,HttpStatus.OK);
	}
	
	private boolean validateSchedule(ScheduleRequest scheduleRequest) {
		
		int breakLengthMins = scheduleRequest.getBreakLengthMins();
		LocalTime opensAt = scheduleRequest.getOpensAt();
		LocalTime closesAt = scheduleRequest.getClosesAt();
		LocalTime breakTime = scheduleRequest.getBreakTime();
		int classHoursPerDay = scheduleRequest.getClassHoursPerDay();
		int classHourLengthMins = scheduleRequest.getClassHourLengthMins();
		int lunchLengthMins = scheduleRequest.getLunchLengthMins();
		LocalTime lunchTime = scheduleRequest.getLunchTime();
		
		
		
		
		
		
		
		
		return true ;
	}

}



