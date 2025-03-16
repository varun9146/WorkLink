package com.worklink.services;

import java.sql.Connection;
import java.util.Optional;
import org.springframework.stereotype.Component;

import com.worklink.model.JobPosterRegistration;
import com.worklink.utills.SaveProfileResponse;

import jakarta.validation.Valid;

@Component
public interface JobPosterServices {

	public Optional<SaveProfileResponse> saveProfile(@Valid JobPosterRegistration profileInput, Connection conn);
}
