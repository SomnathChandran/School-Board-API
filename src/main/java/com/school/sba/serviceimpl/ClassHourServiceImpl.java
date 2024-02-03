package com.school.sba.serviceimpl;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.auditing.CurrentDateTimeProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.enums.ClassStatus;
import com.school.sba.exceptions.AcademicProgramNotFoundByIdException;
import com.school.sba.exceptions.ClassRoomNotFreeException;
import com.school.sba.exceptions.DuplicateClassHoursException;
import com.school.sba.exceptions.InvalidClassHourIdException;
import com.school.sba.exceptions.InvalidUserRoleException;
import com.school.sba.exceptions.ScheduleNotFoundBySchoolIdException;
import com.school.sba.exceptions.SchoolNotAddedToAcademicProgramException;
import com.school.sba.exceptions.SubjectNotFoundExceptionByID;
import com.school.sba.exceptions.UserNotFoundByIdException;
import com.school.sba.repository.AcademicProgramsRepository;
import com.school.sba.repository.ClassHourRepository;
import com.school.sba.repository.SubjectRepository;
import com.school.sba.repository.UserRepository;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ExcelRequestDto;
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
	@Autowired
	private  ResponseStructure<String> responseStructure;
	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponse>>> generateClassHourForAcademicProgram(int programId) {
		return academicRepo.findById(programId).map(program -> {
			School school = program.getSchool();
			if (school == null)
				throw new SchoolNotAddedToAcademicProgramException("school not yet added ");
			Schedule schedule = school.getSchedule();
			List<ClassHourResponse> responses = new ArrayList<>();

			List<ClassHour> classHours = new ArrayList<>();
			if (program.getHours().isEmpty()) {
				if (schedule != null) {
					long classHoursInMinutes = schedule.getClassHourLengthMins().toMinutes();
					int classHoursPerDay = schedule.getClassHoursPerDay();
					LocalTime closesAt = schedule.getClosesAt();
					LocalDateTime currentTime = LocalDateTime.now().with(schedule.getOpensAt());
					LocalTime lunchTimeStart = schedule.getLunchTime();
					LocalTime lunchTimeEnd = lunchTimeStart.plusMinutes(schedule.getLunchLengthMins().toMinutes());
					LocalTime breakTimestart = schedule.getBreakTime();
					LocalTime breakTimeEnd = breakTimestart.plusMinutes(schedule.getBreakLengthMins().toMinutes());
					int days = 7 - currentTime.getDayOfWeek().getValue();

					System.out.println(days + " is the number of days ");

					for (int day = 1; day <= 7 + days; day++) {
						System.out.println(currentTime.getDayOfWeek().name() + " is the day of the week");

						if (!currentTime.getDayOfWeek().equals(school.getHoliday())) {
							for (int hour = 0; hour <= classHoursPerDay + 2; hour++) {
								ClassHour classHour = new ClassHour();
								if (currentTime.toLocalTime().isBefore(closesAt)
										&& !currentTime.toLocalTime().equals(closesAt)) {
									LocalDateTime beginsAt = currentTime;
									LocalDateTime endsAt = currentTime.plusMinutes(classHoursInMinutes);

									if (!currentTime.toLocalTime().equals(lunchTimeStart)
											&& !isLunchTime(currentTime, schedule)) {
										if (!currentTime.toLocalTime().equals(breakTimestart)
												&& !isBreakTime(currentTime, schedule)) {
											classHour.setBeginsAt(beginsAt);
											classHour.setEndsAt(endsAt);
											classHour.setClassStatus(ClassStatus.NOT_SCHEDULE);

											currentTime = endsAt;

										} else {
											classHour.setBeginsAt(beginsAt);
											classHour.setEndsAt(endsAt);
											currentTime = breakTimeEnd.atDate(currentTime.toLocalDate());

											classHour.setClassStatus(ClassStatus.BREAK_TIME);

										}
									} else {
										classHour.setBeginsAt(lunchTimeStart.atDate(currentTime.toLocalDate()));
										classHour.setEndsAt(lunchTimeEnd.atDate(currentTime.toLocalDate()));
										currentTime = lunchTimeEnd.atDate(currentTime.toLocalDate());

										classHour.setClassStatus(ClassStatus.LUNCH_TIME);
									}
									classHour.setAcademicProgram(program);

									ClassHour savedClassHour = classHourRepo.save(classHour);

									classHours.add(savedClassHour);

									responses.add(mapToClassHourResponse(savedClassHour));

								}
							}
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());

						} else {
							currentTime = currentTime.plusDays(1).with(schedule.getOpensAt());
						}

					}
				} else {
					throw new ScheduleNotFoundBySchoolIdException("Schedule Not Found By School!!!");
				}
			} else {
				throw new DuplicateClassHoursException("ClassHour Already Present!!");
			}

			program.setHours(classHours);
			academicRepo.save(program);
			structure.setData(responses);
			structure.setMessage("Added Successfully !!!");
			structure.setStatus(HttpStatus.OK.value());

			return new ResponseEntity<ResponseStructure<List<ClassHourResponse>>>(structure, HttpStatus.OK);
		}).orElseThrow(() -> new AcademicProgramNotFoundByIdException("Academic Program Not Found !!!"));
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

			List<ClassHour> findByEndsALocalTimeBetween = classHourRepo.findByEndsAtBetween(minusDays,minusDays2);
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

	public void autoGenerateClassHourForWeek(int programId)
	{
		generateClassHourForAcademicProgram(programId);
	}



	@Override
	public ResponseEntity<ResponseStructure<String>> generateClassHourInExcel(int programId,ExcelRequestDto excelRequestDto) {

		LocalDateTime from = excelRequestDto.getFromDate().atTime(LocalTime.MIDNIGHT);
		LocalDateTime to = excelRequestDto.getToDate().atTime(LocalTime.MIDNIGHT).plusDays(1);
		
		String folderPath = excelRequestDto.getFilepath().concat("\\ClassHour.xlsx");

			academicRepo.findById(programId).map(program ->{
			List<ClassHour> clist = classHourRepo.findAllByAcademicProgramAndBeginsAtBetween(program,from,to);

			XSSFWorkbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet();
			int rowNumber = 0;
			Row header = sheet.createRow(rowNumber);
			header.createCell(0).setCellValue("Date");
			header.createCell(1).setCellValue("Begin Time");
			header.createCell(2).setCellValue("End Time");
			header.createCell(3).setCellValue("Subject");
			header.createCell(4).setCellValue("Teacher");
			header.createCell(5).setCellValue("Room No");

			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			for (ClassHour classhour : clist) {
				Row row = sheet.createRow(++rowNumber);
				row.createCell(0).setCellValue(dateFormatter.format(classhour.getBeginsAt()));
				row.createCell(1).setCellValue(timeFormatter.format(classhour.getBeginsAt()));
				row.createCell(2).setCellValue(timeFormatter.format(classhour.getEndsAt()));
				if(classhour.getSubject() == null) {
					row.createCell(3).setCellValue("");
				}else {
					row.createCell(3).setCellValue(classhour.getSubject().getSubjectName());
				}
				if(classhour.getUser() == null) {
					row.createCell(4).setCellValue("");
				}else {
					row.createCell(4).setCellValue(classhour.getUser().getUserName());
				}
				row.createCell(5).setCellValue(classhour.getRoomNo());
				
				try {
					workbook.write(new FileOutputStream(folderPath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return "success" ;

		}).orElseThrow(()-> new AcademicProgramNotFoundByIdException("Invalid Academic Program Id") );
	 
	 responseStructure.setStatus(HttpStatus.OK.value());
	 responseStructure.setMessage("Successfully Created");
	 responseStructure.setData("success");
	 return new ResponseEntity<ResponseStructure<String>>(responseStructure,HttpStatus.OK);	
	}




}

