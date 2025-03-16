package com.worklink.controller;

import java.sql.Connection;
import java.sql.SQLException;
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
import com.worklink.model.LabourRegistration;
import com.worklink.model.MobileInput;
import com.worklink.model.ValidateOTP;
import com.worklink.model.VerifyOTP;
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
public class Labour {

	private final ConnectionPool connectionPool;
	private final LabourServices logInService;
	private final ErrorResponseMessages message;

	public Labour(ConnectionPool connectionPool, LabourServices logInService, ErrorResponseMessages message) {
		super();
		this.connectionPool = connectionPool;
		this.logInService = logInService;
		this.message = message;
	}

	@PostMapping("/slp")
	public ResponseEntity<GenericResponseModel<Optional<SaveProfileResponse>, ApiError<String>>> saveLabourProfile(
			@Valid @RequestBody LabourRegistration profileInput) throws SQLException, DataInsertionFailedException {
		try (Connection conn = connectionPool.getConnection()) {
			Optional<SaveProfileResponse> spr = logInService.saveProfile(profileInput, conn);
			if (spr.isPresent()) {
				return new ResponseEntity<>(new GenericResponseModel<>(spr, null), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new GenericResponseModel<>(Optional.empty(), null), HttpStatus.NO_CONTENT);
			}
		} catch (SQLException e) {
			// Handle any exceptions and return an appropriate error response
			ApiError<String> error = new ApiError<>(message.getMessage(Keys.ErrorSavingProfile));
			return new ResponseEntity<>(new GenericResponseModel<>(Optional.empty(), error),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
