package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.serviceimpl.AcademicProgramsServiceImpl;
import com.school.sba.serviceimpl.ClassHourServiceImpl;
import com.school.sba.serviceimpl.SchoolServiceImpl;
import com.school.sba.serviceimpl.UserServiceImpl;

@Component
public class ScheduleJobs {
	
	@Autowired
	private ClassHourServiceImpl classHourService;
	@Autowired
	private AcademicProgramsServiceImpl academicService;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private SchoolServiceImpl schoolService;
	@Autowired
	private AcademicProgramsRepository academicRepo;
	@Transactional
	@Scheduled(fixedDelay =1000l*30)
	public void testMethod()
		{
			userService.permanentDeleteUser();
			academicService.permanentDeleteAcademicProgram();
			schoolService.permanentDeleteSchool();
		}
	
//	@Scheduled(fixedDelay = 1000l*60*60)
	public void autoGenerateClassHour()
	{
		
			classHourService.autoGenerateClassHourForWeek(1);
	}
	
	
	
}
