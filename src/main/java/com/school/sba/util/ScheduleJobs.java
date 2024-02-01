package com.school.sba.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.entity.User;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.serviceimpl.ClassHourServiceImpl;

@Component
public class ScheduleJobs {
	
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AcademicProgramsRepository academicRepo;
	@Autowired
	private ClassHourRepository classHourRepo;
	@Autowired
	private ClassHourServiceImpl classHourService;
	
//	@Scheduled(fixedDelay = 1000l*60)
	public void deleteFromUser() {
		System.out.println("Fetching Users !!");
		 userRepo.findAllByIsDelete(true).forEach(user ->{
			 List<User>uList = new ArrayList<>();
			 user.getPrograms().forEach(program ->{
				 uList.add(null);
				 program.setUsers(uList);
				 academicRepo.save(program);
			 });
			 classHourRepo.findByUser(user).forEach(classHour ->{
				 classHour.setUser(null);
				 classHourRepo.save(classHour);
			 });
			 System.out.println("User Gets Deleted Successfully!!");
			 userRepo.delete(user);
		 });
	}
	
//	@Scheduled(fixedDelay = 1000l*120l)
//	public void deleteAcademicProgram() {
//		System.out.println("Fetching Acadeic Programs");
//		academicRepo.findByIsDelete(true).forEach(Program ->{
//			List<AcademicPrograms> aList = new ArrayList<>();
//				Program.getUsers().forEach(user ->{
//					aList.add(null);
//					user.setPrograms(aList);
//					userRepo.save(user);
//				});
//				classHourRepo.findByAcademicProgram(Program).forEach(classHour ->{
//					classHour.setAcademicProgram(null);
//					classHourRepo.save(classHour);
//				});
//				School school = Program.getSchool();
//				school.setAcademicPrograms(null);
//				Program.setHours(null);
//				academicRepo.delete(Program);
//		});
//	}
	
	public void deleteAcademicProgram() {
		academicRepo.findByIsDelete(true).forEach(program ->{
			
			classHourRepo.deleteAll(program.getHours());
		});
		academicRepo.delete(null);
	}
	
	@Scheduled(fixedDelay = 1000l*120)
	public void autoGenerateClassHour() {
		classHourService.autoGenerateClassHour();
	}
}
