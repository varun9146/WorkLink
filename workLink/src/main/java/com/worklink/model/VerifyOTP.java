package com.worklink.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record VerifyOTP(@NotNull @NotBlank @NotEmpty(message ="{MobileNumber is mandatory}") String mobileNumber,@NotNull @NotBlank @NotEmpty (message ="{CountryCode is mandatory}") String countryCode,
		@NotNull(message="{OTP is mandatory}") Integer otp,Boolean labour) {

}
