package com.school.sba.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.serviceimpl.AcademicProgramsServiceImpl;
import com.school.sba.serviceimpl.ClassHourServiceImpl;
import com.school.sba.serviceimpl.SchoolServiceImpl;
import com.school.sba.serviceimpl.UserServiceImpl;

import jakarta.transaction.Transactional;

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
	
//	@Scheduled(fixedDelay = 1000l*60)
	public void deleteUser() {
		userService.permanentDeleteUser();
	}
//	@Scheduled(fixedDelay = 1000l*60)
	public void deleteSchool() {
		schoolService.permanentDeleteSchool();
	}
	@Transactional
//	@Scheduled(fixedDelay = 1000l*60)
	public void deleteAcademicProgram() {
		academicService.permanentDeleteAcademicProgram();
	}
	
//	@Scheduled(fixedDelay = 1000l*120)
	
	
	
}
