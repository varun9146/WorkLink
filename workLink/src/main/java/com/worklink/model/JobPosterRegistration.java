package com.worklink.model;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record JobPosterRegistration(@NotNull @NotEmpty(message = "Firstname must not be empty") String firstName,

		String lastName,

		@NotNull @NotEmpty(message = "Gender is mandatory") String gender,

		@NotNull @NotEmpty(message = "Date of birth is mandatory") String dob,

		@NotNull(message = "Education is mandatory") Integer education,

		@NotNull(message = "Experience is mandatory") Double experience,

		@NotNull(message = "Per Day charges are mandatory") Double perDayCharges,

		@NotNull @NotEmpty(message = "Location is mandatory") List<Location> location,

		@NotNull @NotEmpty(message = "Country code is mandatory") String countryCode,

		@NotNull @NotEmpty(message = "Number is mandatory") String number,

		@NotNull(message = "Age is mandatory") Integer age) {

}
