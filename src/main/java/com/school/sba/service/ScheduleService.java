package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.Schedule;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.util.ResponseStructure;

public interface ScheduleService {

	ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(int schoolId, ScheduleRequest scheduleRequest);

	ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleOfSchool(int schoolId);

	ResponseEntity<ResponseStructure<ScheduleResponse>> updateSchedule(int scheduleId, ScheduleRequest scheduleRequest);

	ResponseEntity<ResponseStructure<List<ScheduleResponse>>> deleteSchedule(List<Schedule> schedule);


}
