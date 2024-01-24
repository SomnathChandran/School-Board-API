package com.school.sba.requestdto;

import java.util.List;

import com.school.sba.entity.Subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectRequest {
	private List<String>Subjects;

}
