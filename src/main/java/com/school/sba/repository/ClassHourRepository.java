package com.school.sba.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicPrograms;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;

public interface ClassHourRepository extends JpaRepository<ClassHour, Integer> {

	boolean existsByBeginsAtAndRoomNo(LocalDateTime beginsAt, int roomNo);

	List<ClassHour> findByUser(User user);

	List<ClassHour> findByAcademicProgram(AcademicPrograms programId);

	List<ClassHour> findByEndsALocalTimeBetween(LocalDateTime minusDays, LocalDateTime minusDays2);

//	List<ClassHour> findAllByAcademicProgram();

}
