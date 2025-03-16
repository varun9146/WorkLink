package com.worklink.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.worklink.dbconfig.ConnectionPool;
import com.worklink.exception.DataInsertionFailedException;
import com.worklink.model.Education;
import com.worklink.model.EducationInput;
import com.worklink.model.MobileInput;
import com.worklink.model.ValidateOTP;
import com.worklink.model.VerifyOTP;
import com.worklink.services.CommonService;
import com.worklink.services.LabourServices;
import com.worklink.utills.ApiError;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.GenericResponseModel;
import com.worklink.utills.Keys;
import com.worklink.utills.SaveProfileResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/wl", produces = "application/json")
@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "false", allowedHeaders = "*")
@Validated
public class Common {

	private final ConnectionPool connectionPool;
	private final LabourServices logInService;
	private final ErrorResponseMessages message;
	private final CommonService common;

	public Common(ConnectionPool connectionPool, LabourServices logInService, ErrorResponseMessages message,
			CommonService common) {
		super();
		this.connectionPool = connectionPool;
		this.logInService = logInService;
		this.message = message;
		this.common = common;
	}

	@PostMapping(value = "/gotp")
	public ResponseEntity<GenericResponseModel<Optional<SaveProfileResponse>, ApiError<String>>> GenerateOtp(
			@Valid @RequestBody MobileInput mobileInput) throws SQLException {
		try (Connection conn = connectionPool.getConnection()) {
			Optional<SaveProfileResponse> isGeneratedOtp = logInService.generateOTP(mobileInput.mobileNumber(),
					mobileInput.countryCode(), mobileInput.labour(), conn);
			if (isGeneratedOtp.isPresent()
					&& (isGeneratedOtp.get().getMessage() == null || isGeneratedOtp.get().getMessage().isEmpty())) {
				return new ResponseEntity<>(
						new GenericResponseModel<>(null,
								new ApiError<>(message.getMessage(Keys.FAILED_TO_GENERATE_OTP))),
						HttpStatus.UNPROCESSABLE_ENTITY);
			}
			return new ResponseEntity<>(new GenericResponseModel<>(isGeneratedOtp, null), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/votp")
	public ResponseEntity<GenericResponseModel<Optional<ValidateOTP>, ApiError<String>>> verifyOtp(
			@Valid @RequestBody VerifyOTP verifyOtp) throws SQLException, DataInsertionFailedException {
		try (Connection conn = connectionPool.getConnection()) {
			Optional<ValidateOTP> isOtpValid = logInService.verifyOTP(verifyOtp, conn);
			if (!isOtpValid.isPresent()) {
				return new ResponseEntity<>(
						new GenericResponseModel<>(null, new ApiError<>(message.getMessage(Keys.INVALID_OTP))),
						HttpStatus.UNPROCESSABLE_ENTITY);
			}
			return new ResponseEntity<>(new GenericResponseModel<>(isOtpValid, null), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/logOut")
	public ResponseEntity<GenericResponseModel<Optional<SaveProfileResponse>, ApiError<String>>> verifyOtp(
			@RequestAttribute("clientId") int id) throws SQLException, DataInsertionFailedException {
		try (Connection conn = connectionPool.getConnection()) {
			Optional<SaveProfileResponse> isOtpValid = logInService.logout(id, conn);
			if (!isOtpValid.isPresent()) {
				return new ResponseEntity<>(
						new GenericResponseModel<>(null, new ApiError<>(message.getMessage(Keys.LOGOUT_UNSUCESSFULL))),
						HttpStatus.UNPROCESSABLE_ENTITY);
			}
			return new ResponseEntity<>(new GenericResponseModel<>(isOtpValid, null), HttpStatus.OK);
		}
	}

	@PostMapping("/edu")
	public ResponseEntity<GenericResponseModel<Optional<List<Education>>, ApiError<String>>> getEducation(
			@Valid @RequestBody EducationInput profileInput) throws SQLException, DataInsertionFailedException {
		try (Connection conn = connectionPool.getConnection()) {
			ResponseEntity<GenericResponseModel<Optional<List<Education>>, ApiError<String>>> edu= common.getEducation(profileInput, conn);
			Optional<List<Education>> e=edu.getBody().getData();
			if(e.isPresent() && e!= null) {
				return new ResponseEntity<>(new GenericResponseModel<>(e, null),HttpStatus.OK);
			}else {
				return new ResponseEntity<>(
						new GenericResponseModel<>(null, new ApiError<>(message.getMessage(Keys.NODATA))),
						HttpStatus.NOT_FOUND);			
				}
		} catch (SQLException e) {
			// Handle any exceptions and return an appropriate error response
			ApiError<String> error = new ApiError<>(message.getMessage(Keys.ERROR_GETTING_EDUCATIOn));
			return new ResponseEntity<>(new GenericResponseModel<>(Optional.empty(), error),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
