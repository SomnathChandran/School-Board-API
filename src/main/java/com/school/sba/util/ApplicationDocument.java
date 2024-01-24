package com.school.sba.util;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


@Configuration
@OpenAPIDefinition
public class ApplicationDocument {
	
	Info info() {
		return new Info().title("School Board API")
				.version("1.0v")
				.description("School Board API is a RESTful API built using "
						+ "Spring Boot MySQL database");
	}
	
	OpenAPI openAPI() {
		return new OpenAPI().info(info());
	}
	

}
