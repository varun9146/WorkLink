package com.worklink.serviceImpl;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.worklink.dao.Registration;
import com.worklink.exception.InvalidRequestException;
import com.worklink.services.JobPosterServices;
import com.worklink.services.LocationService;
import com.worklink.utills.ErrorKeys;
import com.worklink.utills.ErrorResponseMessages;
import com.worklink.utills.Restutills;
import com.worklink.utills.SaveProfileResponse;

import jakarta.validation.Valid;

@Service
@Validated
public class JobPosterRegistration implements JobPosterServices {

	private final ErrorResponseMessages message;
	private final Registration registration;
	private final Restutills restutills;
	private final LocationService locservice;
	
	public JobPosterRegistration(ErrorResponseMessages message, Registration registration, Restutills restutills,
			LocationService locservice) {
		super();
		this.message = message;
		this.registration = registration;
		this.restutills = restutills;
		this.locservice = locservice;
	}

	@Override
	public Optional<SaveProfileResponse> saveProfile(com.worklink.model.@Valid JobPosterRegistration profileInput,
			Connection conn) {
		System.out.println(" length is " + profileInput.number().length());
		if (profileInput.number().length() != 10) {
			throw new InvalidRequestException(message.getMessage(ErrorKeys.Invalid_Number_Key));
		}
		if (!profileInput.countryCode().equals("+91")) {
			throw new InvalidRequestException(message.getMessage(ErrorKeys.Invalid_Country_code));
		}
		if (registration.checkJobPosterNumberexist(profileInput.number(), conn)) {
			throw new InvalidRequestException(message.getMessage(ErrorKeys.Number_exist));
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = formatter.parse(profileInput.dob());
				System.out.println("Converted Date: " + date);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new InvalidRequestException(message.getMessage(ErrorKeys.Error_getting_number));
			}
			List<Integer> loc = locservice.getlocationIds(profileInput.location(), conn);
			String array = restutills.convertlistToString(loc);
			boolean updated = registration.registerobPoster(profileInput, array, conn);
			if (updated) {
				SaveProfileResponse s = new SaveProfileResponse();
				s.setMessage("Profile Saved Sucessfully");
				return Optional.of(s);
			} else {
				return Optional.empty();
			}
		}
	}


}
