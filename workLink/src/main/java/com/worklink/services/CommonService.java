package com.worklink.services;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.worklink.model.Education;
import com.worklink.model.EducationInput;
import com.worklink.utills.ApiError;
import com.worklink.utills.GenericResponseModel;

public interface CommonService {

	public ResponseEntity<GenericResponseModel<Optional<List<Education>>, ApiError<String>>> getEducation(EducationInput education,Connection conn);
}
