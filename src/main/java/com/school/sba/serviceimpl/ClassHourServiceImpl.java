package com.school.sba.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptions.ClassRoomNotFreeException;
import com.school.sba.exceptions.InvalidClassHourIdException;
import com.school.sba.exceptions.InvalidUserRoleException;
import com.school.sba.exceptions.ScheduleNotFoundBySchoolIdException;
import com.school.sba.exceptions.SubjectNotFoundExceptionByID;
import com.school.sba.exceptions.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.service.ClassHourService;
import com.school.sba.util.ResponseEntityProxy;
import com.school.sba.util.ResponseStructure;
@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private ClassHourRepository classHourRepo;
	@Autowired
	private SubjectRepository subjectRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AcademicProgramsRepository academicRepo;
	@Autowired
	private  ResponseStructure<List<ClassHourResponse>> structure;
	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHourForAcademicProgram(int programId) {
		return academicRepo.findById(programId)
				.map(academicProgarm -> {
					School school = academicProgarm.getSchool();
					Schedule schedule = school.getSchedule();
					if(schedule!=null)
					{
						int classHourPerDay = schedule.getClassHoursPerDay();
						int classHourLength = (int) schedule.getClassHourLengthMins().toMinutes();

						LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());

						LocalTime lunchTimeStart = schedule.getLunchTime();
						LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthMins().toMinutes());
						LocalTime breakTimeStart = schedule.getBreakTime();
						LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthMins().toMinutes());

						for(int day = 1 ; day<=6 ; day++)
						{
							for(int hour = 0;hour<classHourPerDay+2;hour++)
							{
								ClassHour classHour = new ClassHour();

								if(!currentTime.toLocalTime().equals(lunchTimeStart) && !isLunchTime(currentTime, schedule))
								{
									if(!currentTime.toLocalTime().equals(breakTimeStart) && !isBreakTime(currentTime, schedule))
									{
										LocalDateTime beginsAt = currentTime;
										LocalDateTime endsAt = beginsAt.plusMinutes(classHourLength);

										classHour.setBeginsAt(beginsAt);
										classHour.setEndsAt(endsAt);
										classHour.setClassStatus(ClassStatus.NOT_SCHEDULE);

										currentTime = endsAt;
									}
									else
									{
										classHour.setBeginsAt(currentTime);
										classHour.setEndsAt(LocalDateTime.now().with(breakTimeEnd));
										classHour.setClassStatus(ClassStatus.BREAK_TIME);
										currentTime = currentTime.plusMinutes(schedule.getClassHourLengthMins().toMinutes());
									}
								}
								else
								{
									classHour.setBeginsAt(currentTime);
									classHour.setEndsAt(LocalDateTime.now().with(lunchTimeEnd));
									classHour.setClassStatus(ClassStatus.LUNCH_TIME);
									currentTime = currentTime.plusMinutes(schedule.getBreakLengthMins().toMinutes());
								}
								classHour.setAcademicProgram(academicProgarm);
								classHourRepo.save(classHour);
							}
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
						}

					}
					else
						throw new ScheduleNotFoundBySchoolIdException("The school does not contain any schedule, please provide a schedule to the school");

					return ResponseEntityProxy.getResponseEntity(HttpStatus.CREATED, "ClassHour generated successfully for the academic progarm","Class Hour generated for the current week successfully");
				})
				.orElseThrow(() -> new AcademicProgramNotFoundByIdException("Invalid Program Id"));
	}



	private boolean isBreakTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime breakTimeStart = schedule.getBreakTime();
		LocalTime breakTimeEnd = breakTimeStart.plusMinutes(schedule.getBreakLengthMins().toMinutes());
		return (currentTime.toLocalTime().isAfter(breakTimeStart)&& currentTime.toLocalTime().isBefore(breakTimeEnd));
	}

	private boolean isLunchTime(LocalDateTime currentTime, Schedule schedule) {
		LocalTime lunchTimeStart = schedule.getLunchTime();
		LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthMins().toMinutes());

		return (currentTime.toLocalTime().isAfter(lunchTimeStart)&& currentTime.toLocalTime().isBefore(lunchTimeEnd));
	}



	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(
			ArrayList<ClassHourRequest> classHours) {
		ArrayList<ClassHourResponse> alist = new ArrayList<>();
		if (!classHours.isEmpty()) {
			classHours.forEach(classHour -> {
				classHourRepo.findById(classHour.getClassHourId()).map(classHourdb -> {
					if (!classHourRepo.existsByBeginsAtAndRoomNo(classHourdb.getBeginsAt(),
							classHour.getRoomNo())) {
						subjectRepo.findById(classHour.getSubjectId()).map(subject -> {

							if (LocalDateTime.now().isBefore(classHourdb.getBeginsAt())
									&& LocalDateTime.now().isAfter(classHourdb.getBeginsAt())) {
								classHourdb.setRoomNo(classHour.getRoomNo());
								classHourdb.setSubject(subject);
								classHourdb.setClassStatus(ClassStatus.ONGOING);
							} else if (classHourdb.getBeginsAt().toLocalTime().equals(userRepo
									.findById(classHour.getUserId()).get().getSchool().getSchedule().getLunchTime())) {
								classHourdb.setClassStatus(ClassStatus.LUNCH_TIME);
								classHourdb.setSubject(null);

							} else if (classHourdb.getBeginsAt().toLocalTime().equals(userRepo
									.findById(classHour.getUserId()).get().getSchool().getSchedule().getBreakTime())) {
								classHourdb.setClassStatus(ClassStatus.BREAK_TIME);
								classHourdb.setSubject(null);

							} else if (classHourdb.getBeginsAt().isBefore(LocalDateTime.now())) {
								classHourdb.setRoomNo(classHour.getRoomNo());
								classHourdb.setSubject(subject);
								classHourdb.setClassStatus(ClassStatus.COMPLETED);
							} else if (classHourdb.getBeginsAt().isAfter(LocalDateTime.now())) {
								classHourdb.setRoomNo(classHour.getRoomNo());
								classHourdb.setSubject(subject);
								classHourdb.setClassStatus(ClassStatus.UPCOMING);
							}
							return classHourdb;
						}).orElseThrow(() -> new SubjectNotFoundExceptionByID("invalid Subject ID  !!!"));
					} else {
						throw new ClassRoomNotFreeException(" not free !!!");
					}

					userRepo.findById(classHour.getUserId()).map(user -> {

						if (user.getUserRole().toString().equals("TEACHER")) {

							classHourdb.setUser(user);
							return classHourdb;
						} else {
							throw new InvalidUserRoleException("invalid User role !!!TEACHER required ");
						}
					}).orElseThrow(() -> new UserNotFoundByIdException("invalid ID!!!"));
					alist.add(mapToClassHourResponse(classHourRepo.save(classHourdb)));
					structure.setData(alist);
					structure.setMessage("updated successuflly ");
					structure.setStatus(HttpStatus.ACCEPTED.value());
					return classHourdb;
				}).orElseThrow(() -> new InvalidClassHourIdException("invalid ID"));
			});
		}
		return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure, HttpStatus.OK);
	}


	@Override
	public  ResponseEntity<ResponseStructure<List<ClassHourResponse>>>deleteClassHour(List<ClassHour> classHour){
		List<ClassHourResponse>sList = new ArrayList<>();
		classHour.forEach(classhour1 ->{
			classHourRepo.delete(classhour1);
			sList.add(mapToClassHourResponse(classhour1));
		});
		ResponseStructure<List<ClassHourResponse>>structureList = new ResponseStructure<>();
		structureList.setMessage("The ClassHour SuccessFully Gets Deleted!!");
		structureList.setStatus(HttpStatus.OK.value());
		structureList.setData(sList);

		return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structureList,HttpStatus.OK);
	}




	private ClassHourResponse mapToClassHourResponse(ClassHour save) {
		return ClassHourResponse.builder()
				.classHourId(save.getClassHourId())
				.roomNo(save.getRoomNo())
				.classStatus(save.getClassStatus())
				.subject(save.getSubject())
				.beginsAt(save.getBeginsAt())
				.endsAt(save.getEndsAt())
				.build();
	}

	public ClassHour mapToClassHour(ClassHour classHour) {
		return ClassHour.builder().academicProgram(classHour.getAcademicProgram())
				.beginsAt(classHour.getBeginsAt().plusWeeks(1)).classStatus(classHour.getClassStatus())
				.endsAt(classHour.getEndsAt().plusWeeks(1)).roomNo(classHour.getRoomNo())
				.subject(classHour.getSubject()).user(classHour.getUser()).build();
	}



	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForNextWeek(int programId) {
		academicRepo.findById(programId).map(program -> {
			LocalDateTime endsALocalTime = program.getHours().getLast().getEndsAt();
			LocalDateTime minusDays = endsALocalTime.minusDays(5).minusHours(8).minusMinutes(30);
			LocalDateTime minusDays2 = endsALocalTime;
			System.out.println(minusDays + " " + minusDays2);

			List<ClassHour> findByEndsALocalTimeBetween = classHourRepo.findByEndsALocalTimeBetween(minusDays,minusDays2);
			List<ClassHourResponse> clist = new ArrayList<>();

			findByEndsALocalTimeBetween.forEach(find -> {
				clist.add(mapToClassHourResponse(classHourRepo.save(mapToClassHour(find))));
			});
			structure.setData(clist);
			structure.setMessage("Created Next Week Class Hour !!!");
			structure.setStatus(HttpStatus.CREATED.value());
			return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure,HttpStatus.CREATED);

		});
		return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure, HttpStatus.CREATED);

	}





	//	@Override
	//	public void autoGenerateClassHour() {
	//	List<ClassHour> allAcademicProgram = classHourRepo.findAllByAcademicProgram();
	//	allAcademicProgram.forEach(program ->{
	//		int programId = program.getAcademicProgram().getProgramId();
	//		generateClassHourForAcademicProgram(programId);
	//	});
	//				
	//	}
}

