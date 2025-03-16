package com.worklink.serviceImpl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.worklink.dao.GeneralDao;
import com.worklink.model.Education;
import com.worklink.model.EducationInput;
import com.worklink.services.CommonService;
import com.worklink.utills.ApiError;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.GenericResponseModel;
import com.worklink.utills.Keys;
import com.worklink.utills.Restutills;

@Service
@Validated
public class CommonServiceImpl implements CommonService {

	private final ErrorResponseMessages message;
	private final GeneralDao generalDao;
	private final Restutills restutills;

	public CommonServiceImpl(ErrorResponseMessages message, GeneralDao generalDao, Restutills restutills) {
		super();
		this.message = message;
		this.generalDao = generalDao;
		this.restutills = restutills;
	}

	@Override
	public ResponseEntity<GenericResponseModel<Optional<List<Education>>, ApiError<String>>> getEducation(
			EducationInput education, Connection conn) {
		List<Education> edu = new ArrayList<>();
		if (education.education() != null || !education.education().isEmpty()) {
			List<String> columns = new ArrayList<>();
			columns.add(Keys.QUALIFICATION);
			columns.add(Keys.SPECIALIZATION);
			String query = restutills.formQuery(education.education(), Keys.EDUCATION, columns);
			edu = generalDao.getEducation(query, conn);
		} else {
			String query = restutills.formQuery(null, Keys.EDUCATION, null);
			edu = generalDao.getEducation(query, conn);
		}
		if (edu != null && !edu.isEmpty()) {
			return new ResponseEntity<>(new GenericResponseModel<>(Optional.of(edu), null), HttpStatus.OK);
		} else {
			ApiError<String> error = new ApiError<>("No education data available for the given input");
			return new ResponseEntity<>(new GenericResponseModel<>(Optional.empty(), error), HttpStatus.NOT_FOUND);
		}
	}

}
