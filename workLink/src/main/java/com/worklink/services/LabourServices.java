package com.worklink.services;

import java.sql.Connection;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.worklink.model.LabourRegistration;
import com.worklink.model.ValidateOTP;
import com.worklink.model.VerifyOTP;
import com.worklink.utills.SaveProfileResponse;

@Component
public interface LabourServices {

	public Optional<SaveProfileResponse> saveProfile(LabourRegistration labour,Connection conn);
	
	public Optional<SaveProfileResponse> generateOTP(String mobilenumber,String countryCode,Boolean labour, Connection conn);
	
	public Optional<ValidateOTP> verifyOTP(VerifyOTP verifyOTP,Connection conn);
	
	public Optional<SaveProfileResponse> logout(int id,Connection conn);
}
