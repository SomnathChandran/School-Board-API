package com.school.sba.entity;

import java.time.Duration;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int classHoursPerDay;
	private Duration classHourLengthMins;
	private LocalTime breakTime;
	private Duration breakLengthMins;
	private LocalTime lunchTime;
	private Duration lunchLengthMins;
}
