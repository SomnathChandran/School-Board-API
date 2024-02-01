package com.school.sba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicPrograms;

public interface AcademicProgramsRepository extends JpaRepository<AcademicPrograms, Integer> {

	List<AcademicPrograms> findByIsDelete(boolean b);

}
