package com.worklink.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MobileInput(@NotNull @NotBlank @NotEmpty(message ="Mobile number is mandatory") String mobileNumber,@NotNull @NotBlank @NotEmpty(message ="CountryCode number is mandatory") String countryCode, Boolean labour ) {

}
